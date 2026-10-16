package com.music.admin.enums;

/**
 * 管理员状态枚举
 */
public enum AdminStatusEnum {

    DISABLE(0, "禁用"),
    NORMAL(1, "正常");

    private final Integer code;
    private final String desc;

    AdminStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static AdminStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AdminStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescByCode(Integer code) {
        AdminStatusEnum status = getByCode(code);
        return status != null ? status.getDesc() : null;
    }
}
