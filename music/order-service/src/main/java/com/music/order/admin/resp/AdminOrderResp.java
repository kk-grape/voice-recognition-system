package com.music.order.admin.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理员-订单响应类
 */
@Data
public class AdminOrderResp {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orderNo;

    private Date orderAt;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    private String info;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    private BigDecimal amount;

    private Date payAt;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 支付渠道名称
     */
    private String channelName;

    private Date channelAt;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单状态名称
     */
    private String orderStatusName;

    private String orderDesc;

    private Date createdAt;

    private Date updatedAt;
}
