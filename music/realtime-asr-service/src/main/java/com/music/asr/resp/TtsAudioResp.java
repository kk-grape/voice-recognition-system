package com.music.asr.resp;

/**
 * 语音合成响应类
 */
public class TtsAudioResp {

    /**
     * 会话ID（使用 String 避免 JavaScript 大数精度丢失）
     */
    private String sessionId;

    /**
     * 音频文件路径
     */
    private String audioPath;

    /**
     * 字幕类型
     */
    private String subtitleType;

    public TtsAudioResp(String sessionId, String audioPath, String subtitleType) {
        this.sessionId = sessionId;
        this.audioPath = audioPath;
        this.subtitleType = subtitleType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getSubtitleType() {
        return subtitleType;
    }

    public void setSubtitleType(String subtitleType) {
        this.subtitleType = subtitleType;
    }

}
