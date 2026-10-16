package com.music.asr.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AsrSession {

    private Long id;

    /** WebSocket 会话ID（前端连接 ID） */
    private String wsSessionId;

    /** 会员ID */
    private Long memberId;

    /** 会话开始时间 */
    private Date startTime;

    /** 会话结束时间 */
    private Date endTime;

    /** 会话时长（秒） */
    private Integer durationSeconds;

    /** 状态：active / completed / failed */
    private String status;

    /** 源语言，如 zh-CN */
    private String sourceLang;

    /** 源语言描述，如 "普通话" */
    private String sourceLangDesc;

    /** 目标语言，如 en-US */
    private String targetLang;

    /** 目标语言描述，如 "英语" */
    private String targetLangDesc;

    /** 采样率，如 16000 */
    private Integer sampleRate;

    /** 音频格式，如 pcm */
    private String format;

    private Date createdAt;

    private Date updatedAt;
}

