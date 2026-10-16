package com.music.nls.business.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum OrderTypeEnum {

    FILETRANS_PAY("1", "语音识别单次付费"),
    ;

    @Getter
    private String code;

    @Getter
    private String desc;

}
