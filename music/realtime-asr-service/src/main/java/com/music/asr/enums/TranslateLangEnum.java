package com.music.asr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 百度翻译语言枚举类。
 * 每个语言对应一个百度翻译 API 的语言代码
 */
@AllArgsConstructor
public enum TranslateLangEnum {

    LANG0("zh", "中文"),
    LANG1("en", "英文"),
    LANG2("zh", "中英混合"),
    LANG3("jp", "日语"),
    LANG4("th", "泰语"),
    LANG5("kor", "韩语"),
    LANG6("zh", "粤语"),
    LANG7("zh", "四川话"),
    LANG8("zh", "河南话")
    ;

    /**
     * 百度翻译语言代码
     */
    @Getter
    private final String code;

    /**
     * 描述
     */
    @Getter
    private final String desc;

    /**
     * 根据语言代码获取枚举（前端传入的参数）
     */
    public static TranslateLangEnum getByCode(String code) {
        if (code == null) {
            return LANG0;
        }
        try {
            // 支持 LANG0, LANG1 等格式
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 尝试根据百度翻译语言代码匹配
            for (TranslateLangEnum lang : values()) {
                if (lang.getCode().equalsIgnoreCase(code)) {
                    return lang;
                }
            }
            // 尝试根据描述匹配
            for (TranslateLangEnum lang : values()) {
                if (lang.getDesc().equals(code)) {
                    return lang;
                }
            }
            return LANG0;
        }
    }

    /**
     * 根据百度翻译 API 的语言代码获取枚举
     */
    public static TranslateLangEnum getByTranslateCode(String translateCode) {
        if (translateCode == null) {
            return null;
        }
        for (TranslateLangEnum lang : values()) {
            if (lang.getCode().equalsIgnoreCase(translateCode)) {
                return lang;
            }
        }
        return null;
    }
}
