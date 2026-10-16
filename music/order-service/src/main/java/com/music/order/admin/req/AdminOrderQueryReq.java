package com.music.order.admin.req;

import com.music.common.req.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 管理员-订单查询请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminOrderQueryReq extends PageReq {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单号（模糊搜索）
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;
}
