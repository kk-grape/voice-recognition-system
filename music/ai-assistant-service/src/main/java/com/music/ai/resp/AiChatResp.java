package com.music.ai.resp;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * AI 小助手 - 聊天响应
 */
@Data
public class AiChatResp {

    /**
     * 会话UUID
     */
    private String sessionUuid;

    /**
     * 会话标题
     */
    private String sessionTitle;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 消息列表
     */
    private List<MessageDTO> messages;

    /**
     * 聊天消息 DTO
     */
    @Data
    public static class MessageDTO {
        private String id;
        private String role;
        private String content;
        private String aiSkill;
        private String guidanceAction;
        private String guidanceTarget;
        private Date createdAt;
    }
}
