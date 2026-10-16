package com.music.order.admin.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单金额统计响应类
 */
@Data
public class OrderSumResp {

    /**
     * 订单总数量
     */
    private Long totalCount;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
}
