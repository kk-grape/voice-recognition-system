package com.music.asr.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AsrSubtitle {

    private Long id;

    /** 所属会话ID */
    private Long sessionId;

    /** 句子的顺序，从 1 开始 */
    private Integer sentenceIndex;

    /** 识别原文 */
    private String originalText;

    /** 翻译后的文本（可选） */
    private String translatedText;

    /** 置信度（可选） */
    private Double confidence;

    /** 开始时间，毫秒 */
    private Integer startTimeMs;

    /** 结束时间，毫秒 */
    private Integer endTimeMs;

    private Date createdAt;

    private Date updatedAt;
}

