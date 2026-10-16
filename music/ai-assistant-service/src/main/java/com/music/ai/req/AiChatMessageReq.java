package com.music.ai.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 小助手 - 发送消息请求
 */
@Data
public class AiChatMessageReq {

    /**
     * 会话UUID
     */
    @NotNull(message = "会话UUID不能为空")
    private String sessionUuid;

    /**
     * 消息内容
     */
    @NotNull(message = "消息内容不能为空")
    private String content;
}
