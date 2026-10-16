package com.music.ai.resp;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * AI 会议纪要 - 响应
 */
@Data
public class AiMeetingResp {

    /**
     * 纪要记录ID
     */
    private String summaryId;

    /**
     * ASR 会话ID
     */
    private String asrSessionId;

    /**
     * 源语言纪要内容
     */
    private String summarySource;

    /**
     * 目标语言纪要内容
     */
    private String summaryTarget;

    /**
     * 关键要点（JSON 字符串）
     */
    private String keyPoints;

    /**
     * 行动项（JSON 字符串）
     */
    private String actionItems;

    /**
     * 决策结果（JSON 字符串）
     */
    private String decisions;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 对话消息数量
     */
    private Integer messageCount;

    /**
     * 追问对话历史
     */
    private List<MessageDTO> messages;

    /**
     * 消息 DTO
     */
    @Data
    public static class MessageDTO {
        private String id;
        private String role;
        private String content;
        private String aiSkill;
        private Date createdAt;
    }
}
