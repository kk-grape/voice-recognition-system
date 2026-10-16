package com.music.audio.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FiletransLangEnum {

    LANG0("dxksRAMqw2qWxxOG", "普通话"),
    LANG1("kH1y8ns3kk8lXxr0", "英语"),
    LANG2("J8hJxw8XQWUzRVkA", "中英混合"),
    LANG3("yaXsjWbzmAaiHiGD", "日语"),
    LANG4("e5us7Q2KNRkJihfK", "泰语"),
    LANG5("fguvKlRZkpVXZY3W", "韩语"),
    LANG6("v6gsHNemSH95SGGX", "粤语"),
    LANG7("U8r9efQeREfJkg15", "四川话"),
    LANG8("zTw21e2aAyJzxrSp", "河南话")
    ;

    @Getter
    private final String code;

    @Getter
    private final String desc;

}
