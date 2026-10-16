package com.music.member.enums;

import lombok.Getter;

/**
 * 会员等级枚举
 */
@Getter
public enum MemberLevelEnum {
    
    NORMAL(0, "普通用户", "普通用户权限"),
    PREMIUM(1, "高级会员", "高级会员权限，享受更多系统功能"),
    
    ;
    
    private final Integer code;
    private final String name;
    private final String desc;
    
    MemberLevelEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static MemberLevelEnum getByCode(Integer code) {
        for (MemberLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
