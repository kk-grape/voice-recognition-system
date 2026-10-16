package com.music.ai.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * AI 会议纪要 - 追问/修改纪要请求
 */
@Data
public class AiMeetingMessageReq {

    /**
     * ASR 会话ID
     */
    @NotNull(message = "ASR会话ID不能为空")
    private String asrSessionId;

    /**
     * 消息内容（追问或修改指令）
     */
    @NotNull(message = "消息内容不能为空")
    private String content;
}
