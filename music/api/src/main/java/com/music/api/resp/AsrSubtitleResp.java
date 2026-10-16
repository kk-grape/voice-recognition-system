package com.music.api.resp;

import lombok.Data;

/**
 * 字幕数据传输对象，供远程调用返回字幕数据使用。
 */
@Data
public class AsrSubtitleResp {

    /**
     * 字幕记录ID
     */
    private Long id;

    /**
     * ASR 会话ID
     */
    private Long sessionId;

    /**
     * 句子序号
     */
    private Integer sentenceIndex;

    /**
     * 原始文本（识别结果）
     */
    private String originalText;

    /**
     * 翻译文本（如果有设置翻译语言）
     */
    private String translatedText;

    /**
     * 开始时间（毫秒）
     */
    private Integer startTimeMs;

    /**
     * 结束时间（毫秒）
     */
    private Integer endTimeMs;
}
