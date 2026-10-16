package com.music.order.controller;


import com.music.common.req.OrderInfoPayReq;
import com.music.common.resp.CommonResp;
import com.music.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/order-info")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * client
     * 创建订单：仅负责生成订单，不拉起支付。
     * 供音频服务等其他业务服务调用。
     */
    @PostMapping("/create")
    public String create(@RequestBody OrderInfoPayReq req) {
        String orderNo = orderInfoService.createOrder(req);
        return orderNo;
    }

    @GetMapping("/query-order-status/{orderNo}")
    public CommonResp<Object> queryOrderStatus(@PathVariable String orderNo) {
        String status = orderInfoService.queryOrderStatus(orderNo);
        return new CommonResp<>(status);
    }
}

