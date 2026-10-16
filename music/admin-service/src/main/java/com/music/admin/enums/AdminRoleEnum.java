package com.music.admin.enums;

/**
 * 管理员角色枚举
 */
public enum AdminRoleEnum {

    SUPER("super", "超级管理员"),
    ADMIN("admin", "普通管理员"),
    OPERATOR("operator", "操作员");

    private final String code;
    private final String desc;

    AdminRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static AdminRoleEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (AdminRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescByCode(String code) {
        AdminRoleEnum role = getByCode(code);
        return role != null ? role.getDesc() : null;
    }
}
