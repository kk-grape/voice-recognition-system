package com.music.api.client;

import com.music.common.req.OrderInfoPayReq;
import com.music.api.client.fallback.OrderClientFallback;
import com.music.api.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 订单服务对外的 API Client，封装远程调用。
 *
 * 供音频服务等其他业务服务直接调用：
 * - 负责发起远程 HTTP 请求到 order-service
 * - 上层只关心方法调用，不关心远程细节
 * - 通过 fallbackFactory = OrderClientFallback.class 在远程调用失败时给出友好提示
 */
@FeignClient(
        name = "order-service",
        path = "/web/order-info",
        configuration = DefaultFeignConfig.class,
        fallbackFactory = OrderClientFallback.class
)
public interface OrderClient {

    /**
     * 创建订单，只负责落库，不负责拉起支付。
     *
     * @param req 订单创建请求
     * @return 包含订单号的通用响应
     */
    @PostMapping("/create")
    String createOrder(@RequestBody OrderInfoPayReq req);
}

