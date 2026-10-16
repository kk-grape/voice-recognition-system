package com.music.api.client.fallback;

import com.music.api.client.OrderClient;
import com.music.common.req.OrderInfoPayReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 订单服务远程调用失败时的降级处理。
 *
 * 通过实现 {@link FallbackFactory}，可以拿到触发降级的异常信息，
 * 统一记录日志，给上层返回友好的提示，避免异常继续向外抛出。
 */
@Slf4j
@Component
public class OrderClientFallback implements FallbackFactory<OrderClient> {

    @Override
    public OrderClient create(Throwable cause) {
        // 记录远程调用失败原因，便于排查问题
        log.error("调用 order-service 失败，触发 OrderClient 降级逻辑：{}", cause.getMessage(), cause);

        // 返回 OrderClient 的“兜底实现”，避免上层 NPE
        return new OrderClient() {
            @Override
            public String createOrder(OrderInfoPayReq req) {
                // 关键：必须抛异常，才能让上层（含 Seata 全局事务）感知失败并触发回滚
                throw new RuntimeException("调用 order-service 创建订单失败");
            }
        };
    }
}
