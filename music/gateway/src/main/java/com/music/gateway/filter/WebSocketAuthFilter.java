package com.music.gateway.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.music.common.resp.MemberLoginResp;
import com.music.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * WebSocket 认证过滤器
 *
 * 【作用】
 * - 专门拦截 WebSocket 升级请求（Upgrade: websocket）
 * - 对实时语音识别的 WebSocket 连接做 token 鉴权
 * - 鉴权通过后，将会员信息写入请求头传递给下游服务（X-Member-Id / X-Member-Name）
 *
 * 【为什么要单独做一个过滤器】
 * - HTTP 接口已经由 MemberAuthFilter 负责 /web/** 路径的认证
 * - WebSocket 升级请求的路径往往是 /web/ws/** 或类似形式，需要单独处理
 * - 保持“网关统一鉴权、下游服务只依赖 header 中的用户信息”的整体架构风格
 */
@Slf4j
@Component
public class WebSocketAuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 只处理 WebSocket 升级请求
        String upgradeHeader = request.getHeaders().getFirst("Upgrade");
        boolean isWebSocket = "websocket".equalsIgnoreCase(upgradeHeader);
        if (!isWebSocket) {
            return chain.filter(exchange);
        }

        // 只校验与你的实时识别相关的 WebSocket 路径
        // 这里约定前端通过网关访问路径类似：ws://网关域名/ws/asr
        // 如果你的实际路由前缀不同，可以在这里调整判断条件
        if (!path.startsWith("/ws/asr")) {
            return chain.filter(exchange);
        }

        log.info("WebSocket 认证过滤器，path={}，准备进行 token 校验", path);

        // WebSocket 连接使用 URL query 参数中的 token，与普通 HTTP 请求保持一致
        // 尝试从 URL 参数中获取 token
        String token = request.getQueryParams().getFirst("token");
        if (token == null || token.isEmpty()) {
            // 也尝试从 header 中获取（兼容旧版或特殊场景）
            token = request.getHeaders().getFirst("token");
        }
        log.info("WebSocket token 验证开始，token：{}", token != null && !token.isEmpty() ? "已提供" : "未提供");

        if (token == null || token.isEmpty()) {
            log.warn("WebSocket token 为空，请求被拦截");
            return unauthorizedResponse(exchange, "WebSocket token 为空");
        }

        if (!JwtUtil.validate(token)) {
            log.warn("WebSocket token 校验不通过，请求被拦截");
            return unauthorizedResponse(exchange, "WebSocket token 无效或已过期");
        }

        try {
            // 解析 token，获取会员信息
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            log.info("当前 WebSocket 登录会员：{}", loginMember);
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            member.setToken(token);

            // 将会员信息写入请求头，传递给下游 WebSocket 服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Member-Id", String.valueOf(member.getId()))
                    .header("X-Member-Name", member.getName() != null ? member.getName() : "")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("解析 WebSocket 会员 token 失败", e);
            return unauthorizedResponse(exchange, "WebSocket token 解析失败");
        }
    }

    /**
     * 返回未授权响应，阻止 WebSocket 升级
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String body = String.format("{\"success\":false,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 优先级设置在 MemberAuthFilter 之后
        // MemberAuthFilter: -100
        return -80;
    }
}

