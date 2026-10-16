package com.music.ai.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AiMeetingSummary {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 关联的ASR会话ID */
    private Long asrSessionId;

    /** 源语言会议纪要 */
    private String summarySource;

    /** 目标语言会议纪要 */
    private String summaryTarget;

    /** 关键要点（JSON数组） */
    private String keyPoints;

    /** 行动项（JSON数组） */
    private String actionItems;

    /** 决策结果（JSON数组） */
    private String decisions;

    /** 关键词 */
    private String keywords;

    /** 字幕片段数量 */
    private Integer subtitleCount;

    /** 总消耗Token数 */
    private Integer totalTokens;

    private Date createdAt;

    private Date updatedAt;
}
