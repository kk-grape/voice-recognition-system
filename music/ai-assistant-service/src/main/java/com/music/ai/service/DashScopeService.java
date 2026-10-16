package com.music.ai.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.music.ai.dashscope.DashScopeProperties;
import com.music.ai.resp.DashScopeResult;
import com.music.ai.resp.MeetingSummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通义千问 DashScope API 调用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashScopeService {

    private final DashScopeProperties dashScopeProperties;

    /**
     * DashScope 通用 Chat API 地址
     */
    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * 通用对话请求：构造请求体 -> 发送 HTTP POST -> 解析响应
     *
     * @param model        模型名称
     * @param systemPrompt 系统提示词（可为 null）
     * @param messages     对话上下文消息列表
     * @param maxTokens    最大输出 token 数
     * @return AI 回复结果（包含内容、token 统计）
     */
    public DashScopeResult chat(String model, String systemPrompt, List<Map<String, String>> messages, int maxTokens) {
        try {
            // 构造完整的消息列表（system + user + assistant 历史）
            List<Map<String, String>> allMessages = new ArrayList<>();
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                allMessages.add(Map.of("role", "system", "content", systemPrompt));
            }
            allMessages.addAll(messages);

            // 构造请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("messages", allMessages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", dashScopeProperties.getTemperature() != null
                    ? dashScopeProperties.getTemperature() : 0.7);

            // 发送 HTTP POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + dashScopeProperties.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            // 状态码非 200 视为失败
            if (statusCode != 200) {
                log.error("DashScope API 调用失败，状态码：{}，响应：{}", statusCode, body);
                return DashScopeResult.error("API调用失败，状态码：" + statusCode);
            }

            // 解析响应体，提取 AI 回复内容和 token 消耗
            JSONObject respJson = JSON.parseObject(body);
            String content = respJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            JSONObject usage = respJson.getJSONObject("usage");
            int inputTokens = usage.getIntValue("prompt_tokens");
            int outputTokens = usage.getIntValue("completion_tokens");

            log.info("DashScope 调用成功，模型：{}，输入Token：{}，输出Token：{}", model, inputTokens, outputTokens);
            return DashScopeResult.success(content, inputTokens, outputTokens);

        } catch (Exception e) {
            log.error("DashScope API 调用异常", e);
            return DashScopeResult.error("API调用异常：" + e.getMessage());
        }
    }

    /**
     * AI 小助手对话入口（使用通用对话模型 qwen-plus）
     *
     * @param messages 对话上下文（不含 system prompt）
     * @return AI 回复结果
     */
    public DashScopeResult chatAssistant(List<Map<String, String>> messages) {
        String systemPrompt = """
                你是一个活泼可爱的AI助手，名叫小乐。你在一个智能语音识别系统中为用户提供帮助。
                你可以：
                - 回答用户关于系统功能的问题
                - 帮助用户解决使用中遇到的问题
                - 引导用户使用系统的各个功能模块
                - 陪伴用户，提供情感支持

                请用友好、活泼的方式与用户交流。如果用户的问题涉及到具体的系统操作（如上传音频、转写等），
                你可以建议用户访问相应的功能页面。如果用户表达负面情绪，请给予适当的安慰和鼓励。
                """;
        return chat(dashScopeProperties.getChatModel(), systemPrompt, messages,
                dashScopeProperties.getChatMaxTokens() != null ? dashScopeProperties.getChatMaxTokens() : 800);
    }

    /**
     * 会议纪要生成入口（使用长文本摘要模型 qwen-long）
     * 将 ASR 字幕列表发给 AI，生成结构化会议纪要
     *
     * @param subtitles  ASR 识别出的字幕原文列表
     * @param targetLang 目标翻译语言（如 en、zh 等）
     * @return 解析后的结构化会议纪要
     */
    public MeetingSummaryResult generateSummary(List<String> subtitles, String targetLang) {
        try {
            // 将字幕列表拼接为一段文本发给 AI
            String subtitlesText = String.join("\n", subtitles);

            String systemPrompt = """
                    你是一个对话内容总结助手。请根据以下字幕内容，生成一份结构化的对话内容总结。

                    【强制语言规则 - 必须严格遵守】
                    1. 你的所有输出内容（包括 summary_source、summary_target、key_points、action_items、decisions、keywords）都必须基于输入字幕的语言来决定输出语言。
                    2. 如果输入字幕是中文，你就输出中文；如果是英文，就输出英文；如果是日文，就输出日文，以此类推。
                    3. 绝对不要在 key_points、action_items、decisions、keywords 字段中混入其他语言（如英文单词、英文标签等）。

                    【输出格式】
                    必须严格按以下JSON格式返回，不要有任何额外的解释性文字：
                    {
                        "summary_source": "源语言对话内容总结（自然段落描述）",
                        "summary_target": "目标语言对话内容总结（翻译版本）",
                        "key_points": ["要点1（必须是源语言）", "要点2（必须是源语言）"],
                        "action_items": ["待办1（必须是源语言）", "待办2（必须是源语言）"],
                        "decisions": ["结论1（必须是源语言）", "结论2（必须是源语言）"],
                        "keywords": ["关键词1（必须是源语言）", "关键词2（必须是源语言）"]
                    }
                    """;

            List<Map<String, String>> msgs = List.of(
                    Map.of("role", "user", "content", "目标语言是：" + targetLang + "\n\n字幕内容：\n" + subtitlesText));

            DashScopeResult result = chat(dashScopeProperties.getSummaryModel(), systemPrompt, msgs,
                    dashScopeProperties.getSummaryMaxTokens() != null ? dashScopeProperties.getSummaryMaxTokens() : 4000);
            if (!result.isSuccess()) {
                return MeetingSummaryResult.error(result.getErrorMessage());
            }

            // 从 AI 回复中提取 JSON 部分并解析
            String jsonStr = extractJsonFromResponse(result.getContent());
            if (jsonStr == null) {
                return MeetingSummaryResult.error("无法解析会议纪要响应");
            }

            JSONObject json = JSON.parseObject(jsonStr);
            return MeetingSummaryResult.builder()
                    .success(true)
                    .summarySource(json.getString("summary_source"))
                    .summaryTarget(json.getString("summary_target"))
                    .keyPoints(json.getJSONArray("key_points"))
                    .actionItems(json.getJSONArray("action_items"))
                    .decisions(json.getJSONArray("decisions"))
                    .keywords(json.getString("keywords"))
                    .inputTokens(result.getInputTokens())
                    .outputTokens(result.getOutputTokens())
                    .build();

        } catch (Exception e) {
            log.error("生成会议纪要异常", e);
            return MeetingSummaryResult.error("生成会议纪要异常：" + e.getMessage());
        }
    }

    /**
     * 会议纪要追问/修改入口（使用通用对话模型）
     *
     * @param summarySource 源语言纪要（当前版本）
     * @param summaryTarget 目标语言纪要（当前版本）
     * @param messages      对话上下文
     * @return AI 修改后的纪要内容
     */
    public DashScopeResult meetingChat(String summarySource, String summaryTarget, List<Map<String, String>> messages) {
        String systemPrompt = """
                你是一个友善的 AI 语音助手，名叫小慧。

                用户之前的对话内容记录（源语言）如下：
                """ + (summarySource != null ? summarySource : "暂无") + """

                用户之前的对话内容记录（目标语言）如下：
                """ + (summaryTarget != null ? summaryTarget : "暂无") + """

                【你的主要任务：优化对话内容】
                根据用户的反馈或追问，对上面的旧内容进行修改、完善、补充。
                你可以：
                - 根据用户说的话，补充或调整内容
                - 提炼或更新关键词、要点、待办、结论
                - 修正不准确的地方

                【回复风格】
                1. 像朋友聊天一样自然地说，简单告诉我你做了什么调整
                2. 不要有任何正式标记（✅、📋、"场景判断"、"我做了什么"等区块标题）
                3. 口语化，简简单单，轻松随意

                【元数据 - 附加在回复末尾】
                __METADATA__{"summary_source":"优化后的源语言内容（源语言）","summary_target":"优化后的目标语言内容（目标语言）","key_points":["要点1（源语言）","要点2（源语言）"],"action_items":["待办1（源语言）"],"decisions":["结论1（源语言）"],"keywords":["关键词1（源语言）","关键词2（源语言）"]}__METADATA__

                重要：summary_source、summary_target、key_points、action_items、decisions、keywords 全部使用与用户输入字幕相同的语言，不要混入英文。
                """;
        return chat(dashScopeProperties.getChatModel(), systemPrompt, messages,
                dashScopeProperties.getMeetingChatMaxTokens() != null ? dashScopeProperties.getMeetingChatMaxTokens() : 1000);
    }

    /**
     * 从 AI 回复中截取 JSON 部分（AI 返回格式通常为 "说明文字 {json内容}"）
     */
    private String extractJsonFromResponse(String content) {
        if (content == null)
            return null;
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return null;
    }
}
