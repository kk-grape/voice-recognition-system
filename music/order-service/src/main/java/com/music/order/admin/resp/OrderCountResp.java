package com.music.order.admin.resp;

import lombok.Data;

/**
 * 订单统计响应类
 */
@Data
public class OrderCountResp {

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单状态名称
     */
    private String orderStatusName;

    /**
     * 数量
     */
    private Long count;
}
