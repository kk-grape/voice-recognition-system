package com.music.asr.req;

import lombok.Data;

/**
 * TTS音频下载请求类
 */
@Data
public class TtsDownloadReq {

    /**
     * 会话ID（使用 String 避免 JavaScript 大数精度丢失）
     */
    private String sessionId;

    /**
     * 字幕类型：original-原文，translated-翻译
     */
    private String type;
}
