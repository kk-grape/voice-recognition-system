package com.music.nls.business.member.domain;

import java.util.Date;
import lombok.Data;

/**
 * 会员订单实体类
 */
@Data
public class MemberOrder {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long memberId;
    
    /**
     * 套餐ID
     */
    private Long packageId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 金额
     */
    private java.math.BigDecimal amount;
    
    /**
     * 订单状态：0-待支付，1-已支付，2-已取消
     */
    private Integer status;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付时间
     */
    private Date paymentTime;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * 创建时间
     */
    private Date createdAt;
    
    /**
     * 更新时间
     */
    private Date updatedAt;
    
}
