package com.music.ai.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AiChatSession {

    private Long id;

    /** 用户ID */
    private Long userId;

    /** 会话唯一标识 */
    private String sessionUuid;

    /** 会话标题 */
    private String sessionTitle;

    /**
     * 状态：1=正常（前端显示） 2=已删除（前端不显示）
     */
    private Integer status;

    /** 消息总数 */
    private Integer messageCount;

    /** 末条消息预览（前100字） */
    private String lastMessage;

    /** 最后消息时间 */
    private Date lastMessageAt;

    private Date createdAt;

    private Date updatedAt;
}
