package com.music.asr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字幕类型枚举类
 */
@AllArgsConstructor
public enum SubtitleTypeEnum {

    ORIGINAL("original", "原文"),
    TRANSLATED("translated", "翻译");

    /**
     * 类型代码
     */
    @Getter
    private final String code;

    /**
     * 描述
     */
    @Getter
    private final String desc;

    /**
     * 根据代码获取枚举
     */
    public static SubtitleTypeEnum getByCode(String code) {
        if (code == null) {
            return ORIGINAL;
        }
        for (SubtitleTypeEnum type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return ORIGINAL;
    }

}
