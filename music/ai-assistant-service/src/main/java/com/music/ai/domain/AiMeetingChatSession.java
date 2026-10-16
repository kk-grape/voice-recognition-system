package com.music.ai.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AiMeetingChatSession {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 关联的ASR会话ID */
    private Long asrSessionId;

    /**
     * 状态：1=正常 2=已删除
     */
    private Integer status;

    /** 消息总数 */
    private Integer messageCount;

    /** 末条消息预览 */
    private String lastMessage;

    /** 最后消息时间 */
    private Date lastMessageAt;

    private Date createdAt;

    private Date updatedAt;
}
