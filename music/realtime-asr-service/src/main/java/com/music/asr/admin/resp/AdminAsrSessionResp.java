package com.music.asr.admin.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 管理员-实时识别会话响应类
 */
@Data
public class AdminAsrSessionResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String wsSessionId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    private Date startTime;

    private Date endTime;

    /**
     * 会话时长（秒）
     */
    private Integer durationSeconds;

    /**
     * 状态：active / completed / failed
     */
    private String status;

    /**
     * 源语言
     */
    private String sourceLang;

    /**
     * 源语言描述
     */
    private String sourceLangDesc;

    /**
     * 目标语言
     */
    private String targetLang;

    /**
     * 目标语言描述
     */
    private String targetLangDesc;

    /**
     * 采样率
     */
    private Integer sampleRate;

    /**
     * 音频格式
     */
    private String format;

    private Date createdAt;

    private Date updatedAt;
}
