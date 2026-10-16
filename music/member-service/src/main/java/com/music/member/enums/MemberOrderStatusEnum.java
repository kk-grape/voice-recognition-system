package com.music.member.enums;

import lombok.Getter;

/**
 * 会员订单状态枚举
 */
@Getter
public enum MemberOrderStatusEnum {
    
    PENDING(0, "待支付", "订单已创建，等待用户支付"),
    PAID(1, "已支付", "订单已支付，会员状态已更新"),
    CANCELLED(2, "已取消", "订单已取消"),
    
    ;
    
    private final Integer code;
    private final String name;
    private final String desc;
    
    MemberOrderStatusEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static MemberOrderStatusEnum getByCode(Integer code) {
        for (MemberOrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
