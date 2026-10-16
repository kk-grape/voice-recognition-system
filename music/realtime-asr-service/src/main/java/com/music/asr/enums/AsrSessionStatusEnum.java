package com.music.asr.enums;

public enum AsrSessionStatusEnum {
    ACTIVE("active", "进行中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "失败");

    private String code;

    private String desc;

    AsrSessionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static AsrSessionStatusEnum getByCode(String code) {
        for (AsrSessionStatusEnum e : AsrSessionStatusEnum.values()) {
            if (code != null && code.equals(e.getCode())) {
                return e;
            }
        }
        return null;
    }
}
