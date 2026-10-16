package com.music.ai.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.music.ai.domain.AiMeetingChatMessage;
import com.music.ai.domain.AiMeetingChatSession;
import com.music.ai.domain.AiMeetingSummary;
import com.music.ai.mapper.AiMeetingChatMessageMapper;
import com.music.ai.mapper.AiMeetingChatSessionMapper;
import com.music.ai.mapper.AiMeetingSummaryMapper;
import com.music.ai.resp.AiMeetingResp;
import com.music.ai.resp.DashScopeResult;
import com.music.ai.resp.MeetingSummaryResult;
import com.music.api.client.RealtimeAsrClient;
import com.music.api.resp.AsrSubtitleResp;
import com.music.common.resp.CommonResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 会议纪要 - 会议纪要服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiMeetingService {

    private final AiMeetingSummaryMapper aiMeetingSummaryMapper;
    private final AiMeetingChatSessionMapper aiMeetingChatSessionMapper;
    private final AiMeetingChatMessageMapper aiMeetingChatMessageMapper;
    private final RealtimeAsrClient realtimeAsrClient;
    private final DashScopeService dashScopeService;

    /**
     * 生成会议纪要（主功能）
     * 流程：获取字幕数据 -> 调用 AI 生成纪要 -> 保存纪要和对话记录
     *
     * @param userId      用户ID
     * @param asrSessionId ASR 会话ID
     * @param targetLang  目标翻译语言
     * @return 生成的会议纪要
     */
    @Transactional
    public AiMeetingResp generateSummary(Long userId, Long asrSessionId, String targetLang) {
        // 通过 Feign 调用 realtime-asr-service 获取该会话的字幕列表
        CommonResp<List<AsrSubtitleResp>> resp = realtimeAsrClient.listSubtitles(asrSessionId);
        if (!resp.isSuccess() || resp.getContent() == null || resp.getContent().isEmpty()) {
            throw new RuntimeException("获取字幕数据失败，ASR会话可能没有识别记录");
        }

        // 提取字幕原文（用于发给 AI）
        List<String> subtitles = resp.getContent().stream()
                .map(AsrSubtitleResp::getOriginalText)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 调用通义千问生成会议纪要
        MeetingSummaryResult aiResult = dashScopeService.generateSummary(subtitles, targetLang);
        if (!aiResult.isSuccess()) {
            throw new RuntimeException("AI生成会议纪要失败：" + aiResult.getErrorMessage());
        }

        Date now = DateUtil.date();

        // 创建/查找该 ASR 会话对应的 AI 追问会话
        AiMeetingChatSession chatSession = aiMeetingChatSessionMapper.selectByAsrSessionId(asrSessionId);
        if (chatSession == null) {
            chatSession = new AiMeetingChatSession();
            chatSession.setId(IdUtil.getSnowflakeNextId());
            chatSession.setUserId(userId);
            chatSession.setAsrSessionId(asrSessionId);
            chatSession.setStatus(1);
            chatSession.setMessageCount(0);
            chatSession.setCreatedAt(now);
            chatSession.setUpdatedAt(now);
            aiMeetingChatSessionMapper.insert(chatSession);
        }

        // 保存 AI 生成的纪要消息
        AiMeetingChatMessage aiMsg = new AiMeetingChatMessage();
        aiMsg.setId(IdUtil.getSnowflakeNextId());
        aiMsg.setSessionId(chatSession.getId());
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiResult.getSummarySource());
        aiMsg.setAiSkill("generate");
        aiMsg.setInputTokens(aiResult.getInputTokens());
        aiMsg.setOutputTokens(aiResult.getOutputTokens());
        aiMsg.setCreatedAt(now);
        aiMeetingChatMessageMapper.insert(aiMsg);

        // 更新会话末条消息预览
        aiMeetingChatSessionMapper.updateLastMessage(chatSession.getId(),
                StrUtil.sub(aiResult.getSummarySource(), 0, 100), now, 1);

        // 创建/更新会议纪要主记录
        boolean isNew = false;
        AiMeetingSummary summary = aiMeetingSummaryMapper.selectByAsrSessionId(asrSessionId);
        if (summary == null) {
            summary = new AiMeetingSummary();
            summary.setId(IdUtil.getSnowflakeNextId());
            summary.setUserId(userId);
            summary.setAsrSessionId(asrSessionId);
            summary.setCreatedAt(now);
            summary.setUpdatedAt(now);
            isNew = true;
        }

        summary.setSummarySource(aiResult.getSummarySource());
        summary.setSummaryTarget(aiResult.getSummaryTarget());
        summary.setKeyPoints(aiResult.getKeyPoints() != null ? aiResult.getKeyPoints().toJSONString() : null);
        summary.setActionItems(aiResult.getActionItems() != null ? aiResult.getActionItems().toJSONString() : null);
        summary.setDecisions(aiResult.getDecisions() != null ? aiResult.getDecisions().toJSONString() : null);
        summary.setKeywords(aiResult.getKeywords());
        summary.setSubtitleCount(subtitles.size());
        summary.setTotalTokens(aiResult.getInputTokens() + aiResult.getOutputTokens());
        summary.setUpdatedAt(now);

        // 插入或更新纪要记录
        if (isNew) {
            aiMeetingSummaryMapper.insert(summary);
        } else {
            aiMeetingSummaryMapper.updateSummary(summary.getId(),
                    summary.getSummarySource(), summary.getSummaryTarget(),
                    summary.getKeyPoints(), summary.getActionItems(),
                    summary.getDecisions(), summary.getKeywords(),
                    summary.getTotalTokens());
        }

        log.info("生成会议纪要成功，用户：{}，asrSessionId：{}，字幕数：{}", userId, asrSessionId, subtitles.size());

        // 重新从数据库查询最新的会话信息，确保数据一致性
        AiMeetingChatSession updatedChatSession = aiMeetingChatSessionMapper.selectByAsrSessionId(asrSessionId);
        return toAiMeetingResp(summary, updatedChatSession, Collections.emptyList());
    }

    /**
     * 追问/修改会议纪要
     * 流程：保存用户追问 -> 调用 AI 修改纪要 -> 保存 AI 回复 -> 尝试同步更新纪要内容
     *
     * @param userId      用户ID
     * @param asrSessionId ASR 会话ID
     * @param content     用户追问内容
     * @return 更新后的纪要及对话历史
     */
    @Transactional
    public AiMeetingResp sendMessage(Long userId, Long asrSessionId, String content) {
        // 校验是否已生成过纪要
        AiMeetingChatSession chatSession = aiMeetingChatSessionMapper.selectByAsrSessionId(asrSessionId);
        if (chatSession == null) {
            throw new RuntimeException("该ASR会话还没有生成过会议纪要，请先点击「AI 会议纪要」生成");
        }

        Date now = DateUtil.date();

        // 保存用户消息
        AiMeetingChatMessage userMsg = new AiMeetingChatMessage();
        userMsg.setId(IdUtil.getSnowflakeNextId());
        userMsg.setSessionId(chatSession.getId());
        userMsg.setRole("user");
        userMsg.setContent(content);
        userMsg.setCreatedAt(now);
        aiMeetingChatMessageMapper.insert(userMsg);

        // 查询历史，构造上下文
        List<AiMeetingChatMessage> history = aiMeetingChatMessageMapper.selectBySessionId(chatSession.getId());
        AiMeetingSummary summary = aiMeetingSummaryMapper.selectByAsrSessionId(asrSessionId);

        List<Map<String, String>> contextMessages = history.stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .collect(Collectors.toList());
        contextMessages.add(Map.of("role", "user", "content", content));

        // 调用 AI 追问接口
        DashScopeResult result = dashScopeService.meetingChat(
                summary != null ? summary.getSummarySource() : null,
                summary != null ? summary.getSummaryTarget() : null,
                contextMessages);

        // AI 回复内容
        String aiRawContent;
        if (!result.isSuccess()) {
            aiRawContent = "抱歉，AI 暂时无法回复，请稍后再试。";
        } else {
            aiRawContent = result.getContent();
        }

        // 先从 AI 回复中解析出纯文本（用于显示）和元数据（用于更新数据库）
        String aiDisplayContent = stripMetadataFromContent(aiRawContent);
        String jsonStr = extractMetadataFromResponse(aiRawContent);

        // 尝试从 AI 回复中解析元数据并更新数据库
        if (summary != null && jsonStr != null) {
            try {
                JSONObject updated = JSON.parseObject(jsonStr);
                String newSource = updated.getString("summary_source");
                String newTarget = updated.getString("summary_target");
                String keyPoints = updated.getJSONArray("key_points") != null
                        ? updated.getJSONArray("key_points").toJSONString()
                        : summary.getKeyPoints();
                String actionItems = updated.getJSONArray("action_items") != null
                        ? updated.getJSONArray("action_items").toJSONString()
                        : summary.getActionItems();
                String decisions = updated.getJSONArray("decisions") != null
                        ? updated.getJSONArray("decisions").toJSONString()
                        : summary.getDecisions();
                String keywords = updated.getString("keywords") != null ? updated.getString("keywords")
                        : summary.getKeywords();

                if (newSource != null)
                    summary.setSummarySource(newSource);
                if (newTarget != null)
                    summary.setSummaryTarget(newTarget);
                summary.setKeyPoints(keyPoints);
                summary.setActionItems(actionItems);
                summary.setDecisions(decisions);
                summary.setKeywords(keywords);
                summary.setTotalTokens(summary.getTotalTokens() + result.getInputTokens() + result.getOutputTokens());
                summary.setUpdatedAt(now);
                aiMeetingSummaryMapper.updateSummary(summary.getId(),
                        summary.getSummarySource(), summary.getSummaryTarget(),
                        summary.getKeyPoints(), summary.getActionItems(),
                        summary.getDecisions(), summary.getKeywords(),
                        summary.getTotalTokens());
            } catch (Exception e) {
                log.warn("解析AI修改建议失败，忽略同步更新纪要", e);
            }
        }

        // 保存 AI 回复（只存纯文本，不含 JSON 标记）
        AiMeetingChatMessage aiMsg = new AiMeetingChatMessage();
        aiMsg.setId(IdUtil.getSnowflakeNextId());
        aiMsg.setSessionId(chatSession.getId());
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiDisplayContent);
        aiMsg.setAiSkill("modify");
        aiMsg.setInputTokens(result.getInputTokens());
        aiMsg.setOutputTokens(result.getOutputTokens());
        aiMsg.setCreatedAt(DateUtil.date());
        aiMeetingChatMessageMapper.insert(aiMsg);

        // 重新查询消息数量，确保准确
        List<AiMeetingChatMessage> allMessages = aiMeetingChatMessageMapper.selectBySessionId(chatSession.getId());
        int newCount = allMessages.size();

        // 更新会话消息计数和末条预览（使用纯文本）
        aiMeetingChatSessionMapper.updateLastMessage(chatSession.getId(),
                StrUtil.sub(aiDisplayContent, 0, 100), now, newCount);

        // 重新查询，返回最新状态
        summary = aiMeetingSummaryMapper.selectByAsrSessionId(asrSessionId);
        // 重新查询最新的会话信息，确保 messageCount 等字段是最新的
        AiMeetingChatSession updatedChatSession = aiMeetingChatSessionMapper.selectByAsrSessionId(asrSessionId);

        log.info("会议纪要追问成功，用户：{}，asrSessionId：{}", userId, asrSessionId);
        return toAiMeetingResp(summary, updatedChatSession, allMessages);
    }

    /**
     * 获取会议纪要及追问对话历史
     *
     * @param userId      用户ID
     * @param asrSessionId ASR 会话ID
     * @return 会议纪要及对话历史
     */
    public AiMeetingResp getMeetingHistory(Long userId, Long asrSessionId) {
        AiMeetingSummary summary = aiMeetingSummaryMapper.selectByAsrSessionId(asrSessionId);
        AiMeetingChatSession chatSession = aiMeetingChatSessionMapper.selectByAsrSessionId(asrSessionId);
        if (chatSession == null) {
            throw new RuntimeException("该ASR会话还没有生成过会议纪要");
        }
        List<AiMeetingChatMessage> messages = aiMeetingChatMessageMapper.selectBySessionId(chatSession.getId());
        return toAiMeetingResp(summary, chatSession, messages);
    }

    /**
     * 将数据库实体转换为前端响应格式
     */
    private AiMeetingResp toAiMeetingResp(AiMeetingSummary summary, AiMeetingChatSession chatSession,
            List<AiMeetingChatMessage> messages) {
        AiMeetingResp resp = new AiMeetingResp();
        if (summary != null) {
            resp.setSummaryId(String.valueOf(summary.getId()));
            resp.setAsrSessionId(String.valueOf(summary.getAsrSessionId()));
            resp.setSummarySource(summary.getSummarySource());
            resp.setSummaryTarget(summary.getSummaryTarget());
            resp.setKeyPoints(summary.getKeyPoints());
            resp.setActionItems(summary.getActionItems());
            resp.setDecisions(summary.getDecisions());
            resp.setKeywords(summary.getKeywords());
        }
        resp.setMessageCount(chatSession != null ? chatSession.getMessageCount() : 0);
        resp.setMessages(messages.stream().map(m -> {
            AiMeetingResp.MessageDTO dto = new AiMeetingResp.MessageDTO();
            dto.setId(String.valueOf(m.getId()));
            dto.setRole(m.getRole());
            dto.setContent(m.getContent());
            dto.setAiSkill(m.getAiSkill());
            dto.setCreatedAt(m.getCreatedAt());
            return dto;
        }).collect(Collectors.toList()));
        return resp;
    }

    /**
     * 从 AI 回复中提取隐藏的元数据 JSON（格式：__METADATA__{...}__METADATA__）
     */
    private String extractMetadataFromResponse(String content) {
        if (content == null)
            return null;
        String marker = "__METADATA__";
        int start = content.indexOf(marker);
        if (start < 0)
            return null;
        start += marker.length();
        int end = content.lastIndexOf(marker);
        if (end > start) {
            return content.substring(start, end);
        }
        return null;
    }

    /**
     * 去除消息内容中的隐藏 JSON 标记
     */
    private String stripMetadataFromContent(String content) {
        if (content == null)
            return null;
        return content.replaceAll("__METADATA__\\{.*?\\}__METADATA__", "").trim();
    }
}
