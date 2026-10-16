package com.music.asr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阿里云语音识别语言枚举类。
 * 每个语言对应一个阿里云 NLS AppKey
 */
@AllArgsConstructor
public enum AsrLangEnum {

    LANG0("dxksRAMqw2qWxxOG", "zh-CN", "普通话"),
    LANG1("kH1y8ns3kk8lXxr0", "en-US", "英语"),
    LANG2("J8hJxw8XQWUzRVkA", "zh-CN", "中英混合"),
    LANG3("yaXsjWbzmAaiHiGD", "ja-JP", "日语"),
    LANG4("e5us7Q2KNRkJihfK", "th-TH", "泰语"),
    LANG5("fguvKlRZkpVXZY3W", "ko-KR", "韩语"),
    LANG6("v6gsHNemSH95SGGX", "zh-CN", "粤语"),
    LANG7("U8r9efQeREfJkg15", "zh-CN", "四川话"),
    LANG8("zTw21e2aAyJzxrSp", "zh-CN", "河南话");

    /**
     * 阿里云 NLS AppKey
     */
    @Getter
    private final String appKey;

    /**
     * 阿里云语言代码（用于 WebSocket 连接参数）
     */
    @Getter
    private final String nlsLanguage;

    /**
     * 描述
     */
    @Getter
    private final String desc;

    /**
     * 根据 AppKey 获取枚举
     */
    public static AsrLangEnum getByAppKey(String appKey) {
        for (AsrLangEnum lang : values()) {
            if (lang.getAppKey().equals(appKey)) {
                return lang;
            }
        }
        return null;
    }

    /**
     * 根据语言代码获取枚举（前端传入的参数）
     */
    public static AsrLangEnum getByCode(String code) {
        if (code == null) {
            return LANG0;
        }
        try {
            // 支持 LANG0, LANG1 等格式
            return valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 尝试根据 NLS 语言代码匹配（zh-CN, en-US, ja-JP 等）
            for (AsrLangEnum lang : values()) {
                if (lang.getNlsLanguage().equalsIgnoreCase(code)) {
                    return lang;
                }
            }
            // 尝试根据描述匹配
            for (AsrLangEnum lang : values()) {
                if (lang.getDesc().equals(code)) {
                    return lang;
                }
            }
            return LANG0;
        }
    }

    /**
     * 根据数据库存储的语言代码获取枚举（如 lang0, lang1, lang2 等）
     * 数据库存储格式为 lang0, lang1, lang2...
     */
    public static AsrLangEnum getByNlsLanguage(String nlsLanguage) {
        if (nlsLanguage == null) {
            return LANG0;
        }
        try {
            // 数据库存储格式为 lang0, lang1, lang2 等，转换为大写后匹配枚举名
            return valueOf(nlsLanguage.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
