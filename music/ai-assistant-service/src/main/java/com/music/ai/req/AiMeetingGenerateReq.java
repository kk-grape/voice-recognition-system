package com.music.ai.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

    /**
     * AI 会议纪要 - 生成纪要请求
     */
    @Data
    public class AiMeetingGenerateReq {

        /**
         * ASR 会话ID
         */
        @NotNull(message = "ASR会话ID不能为空")
        private String asrSessionId;

        /**
         * 目标语言（默认 en）
         */
        private String targetLang = "en";
    }
