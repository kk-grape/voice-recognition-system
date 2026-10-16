package com.music.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum OrderInfoChannelEnum {

    ALIPAY("A", "支付宝"),
    WXPAY("W", "微信");

    @Getter
    private String code;

    @Getter
    private String desc;

    public static OrderInfoChannelEnum getByCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
