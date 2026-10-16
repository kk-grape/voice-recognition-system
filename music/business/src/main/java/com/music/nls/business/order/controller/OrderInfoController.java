package com.music.nls.business.order.controller;

import com.music.nls.business.common.resp.CommonResp;
import com.music.nls.business.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/order-info")
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;

    @GetMapping("/query-order-status/{orderNo}")
    public CommonResp<Object> sendForRegister(@PathVariable String orderNo) {
        String status = orderInfoService.queryOrderStatus(orderNo);
        return new CommonResp<>(status);
    }
}
