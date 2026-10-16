package com.music.ai.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.music.ai.dashscope.DashScopeProperties;
import com.music.ai.domain.AiChatMessage;
import com.music.ai.domain.AiChatSession;
import com.music.ai.mapper.AiChatMessageMapper;
import com.music.ai.mapper.AiChatSessionMapper;
import com.music.ai.resp.AiChatResp;
import com.music.ai.resp.DashScopeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 小助手 - 聊天服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final AiChatSessionMapper chatSessionMapper;
    private final AiChatMessageMapper chatMessageMapper;
    private final DashScopeService dashScopeService;
    private final DashScopeProperties dashScopeProperties;

    /**
     * 获取最近会话（点开聊天框时调用）
     * - 有会话：返回最近一次 + 消息列表
     * - 无会话：自动创建新会话
     *
     * @param userId 用户ID
     * @return 会话信息及消息列表
     */
    @Transactional
    public AiChatResp getRecentSession(Long userId) {
        // 查询该用户最近一条未删除的会话记录
        AiChatSession recent = chatSessionMapper.selectRecentByUserId(userId);
        if (recent == null) {
            return createSession(userId);
        }
        // 查询该会话下的所有消息
        List<AiChatMessage> messages = chatMessageMapper.selectBySessionId(recent.getId());
        return toAiChatResp(recent, messages);
    }

    /**
     * 新建一个空会话
     *
     * @param userId 用户ID
     * @return 新会话信息
     */
    @Transactional
    public AiChatResp createSession(Long userId) {
        Date now = DateUtil.date();
        AiChatSession session = new AiChatSession();
        session.setId(IdUtil.getSnowflakeNextId());
        session.setUserId(userId);
        session.setSessionUuid(UUID.randomUUID().toString().replace("-", ""));
        session.setSessionTitle("新对话");
        session.setStatus(1);
        session.setMessageCount(0);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        chatSessionMapper.insert(session);
        log.info("创建新会话，用户：{}，sessionUuid：{}", userId, session.getSessionUuid());
        return toAiChatResp(session, Collections.emptyList());
    }

    /**
     * 发送消息：保存用户消息 -> 调用 AI -> 保存 AI 回复 -> 更新会话
     *
     * @param userId      用户ID
     * @param sessionUuid 会话UUID
     * @param content     用户消息内容
     * @return 更新后的会话信息（含消息列表）
     */
    @Transactional
    public AiChatResp sendMessage(Long userId, String sessionUuid, String content) {
        // 校验会话归属
        AiChatSession session = chatSessionMapper.selectByUuid(sessionUuid);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权限");
        }
        Date now = DateUtil.date();

        // 保存用户消息
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setId(IdUtil.getSnowflakeNextId());
        userMsg.setSessionId(session.getId());
        userMsg.setRole("user");
        userMsg.setContent(content);
        userMsg.setCreatedAt(now);
        chatMessageMapper.insert(userMsg);

        // 构造上下文发给 AI（取最近10条历史消息）
        List<AiChatMessage> history = chatMessageMapper.selectBySessionId(session.getId());
        List<Map<String, String>> contextMessages = buildContext(history);
        DashScopeResult result = dashScopeService.chatAssistant(contextMessages);

        // AI 回复内容（失败时返回友好提示）
        String aiContent;
        if (!result.isSuccess()) {
            aiContent = "抱歉，AI 暂时无法回复，请稍后再试。错误信息：" + result.getErrorMessage();
        } else {
            aiContent = result.getContent();
        }

        // 保存 AI 回复消息
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setId(IdUtil.getSnowflakeNextId());
        aiMsg.setSessionId(session.getId());
        aiMsg.setRole("assistant");
        aiMsg.setContent(aiContent);
        aiMsg.setInputTokens(result.getInputTokens());
        aiMsg.setOutputTokens(result.getOutputTokens());
        aiMsg.setAiSkill("chat");
        aiMsg.setCreatedAt(DateUtil.date());
        chatMessageMapper.insert(aiMsg);

        // 重新查询全部消息数量，更新会话的预览和总数
        history = chatMessageMapper.selectBySessionId(session.getId());
        int newCount = history.size();
        chatSessionMapper.updateLastMessage(session.getId(),
                StrUtil.sub(aiContent, 0, 100), now, newCount);

        // 重新查询，返回最新状态
        session = chatSessionMapper.selectByUuid(sessionUuid);
        return toAiChatResp(session, history);
    }

    /**
     * 获取指定会话的历史消息（供前端回显）
     *
     * @param userId      用户ID
     * @param sessionUuid 会话UUID
     * @return 会话信息及消息列表
     */
    public AiChatResp getSessionHistory(Long userId, String sessionUuid) {
        AiChatSession session = chatSessionMapper.selectByUuid(sessionUuid);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权限");
        }
        List<AiChatMessage> messages = chatMessageMapper.selectBySessionId(session.getId());
        return toAiChatResp(session, messages);
    }

    /**
     * 删除会话（软删除：将状态置为 2）
     *
     * @param userId      用户ID
     * @param sessionUuid 会话UUID
     */
    @Transactional
    public void deleteSession(Long userId, String sessionUuid) {
        AiChatSession session = chatSessionMapper.selectByUuid(sessionUuid);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权限");
        }
        chatSessionMapper.updateStatus(session.getId(), 2);
        log.info("删除会话，用户：{}，sessionUuid：{}", userId, sessionUuid);
    }

    /**
     * 构造发给 AI 的上下文消息列表（取最近 max 条，从配置读取）
     */
    private List<Map<String, String>> buildContext(List<AiChatMessage> history) {
        int max = dashScopeProperties.getChatMaxContextMessages() != null
                ? dashScopeProperties.getChatMaxContextMessages() : 10;
        int from = Math.max(0, history.size() - max);
        return history.subList(from, history.size()).stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .collect(Collectors.toList());
    }

    /**
     * 将数据库实体转换为前端响应格式
     */
    private AiChatResp toAiChatResp(AiChatSession session, List<AiChatMessage> messages) {
        AiChatResp resp = new AiChatResp();
        resp.setSessionUuid(session.getSessionUuid());
        resp.setSessionTitle(session.getSessionTitle());
        resp.setMessageCount(session.getMessageCount());
        resp.setMessages(messages.stream().map(m -> {
            AiChatResp.MessageDTO dto = new AiChatResp.MessageDTO();
            dto.setId(String.valueOf(m.getId()));
            dto.setRole(m.getRole());
            dto.setContent(m.getContent());
            dto.setAiSkill(m.getAiSkill());
            dto.setGuidanceAction(m.getGuidanceAction());
            dto.setGuidanceTarget(m.getGuidanceTarget());
            dto.setCreatedAt(m.getCreatedAt());
            return dto;
        }).collect(Collectors.toList()));
        return resp;
    }
}
