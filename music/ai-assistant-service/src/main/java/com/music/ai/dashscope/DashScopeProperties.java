package com.music.ai.dashscope;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DashScope（通义千问）配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "dashscope")
public class DashScopeProperties {

    /**
     * DashScope API 密钥
     */
    private String apiKey;

    /**
     * 通用对话模型（AI 小助手使用）
     */
    private String chatModel;

    /**
     * 长文本摘要模型（会议纪要使用）
     */
    private String summaryModel;

    /**
     * 最大上下文消息条数
     */
    private Integer maxContextMessages;

    /**
     * AI 小助手最大历史消息条数（默认取最近10条）
     */
    private Integer chatMaxContextMessages;

    /**
     * AI 小助手每次最大输出 token 数
     */
    private Integer chatMaxTokens;

    /**
     * 会议纪要每次最大输出 token 数
     */
    private Integer summaryMaxTokens;

    /**
     * 会议纪要追问每次最大输出 token 数
     */
    private Integer meetingChatMaxTokens;

    /**
     * 模型推理温度（0-1，越高越随机，越低越确定）
     */
    private Double temperature;
}
