package com.music.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum OrderTypeEnum {

    FILETRANS_PAY("1", "语音识别单次付费"),
    ;

    @Getter
    private String code;

    @Getter
    private String desc;

    public static OrderTypeEnum getByCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
