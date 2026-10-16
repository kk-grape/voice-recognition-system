package com.music.gateway.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.music.common.resp.AdminLoginResp;
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
import java.util.Arrays;
import java.util.List;

/**
 * 管理员认证过滤器
 * 拦截 /admin/** 路径的请求，验证管理员token
 */
@Slf4j
@Component
public class AdminAuthFilter implements GlobalFilter, Ordered {

    /**
     * 不需要认证的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/admin/kaptcha/image-code",
            "/admin/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 只处理 /admin/** 路径的请求
        if (!path.startsWith("/admin/")) {
            return chain.filter(exchange);
        }
        
        log.info("管理员认证过滤器，path：{}", path);

        // 检查是否为排除路径
        if (isExcludePath(path)) {
            log.info("路径 {} 不需要认证，直接放行", path);
            return chain.filter(exchange);
        }

        // OPTIONS请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        // 获取token
        String token = request.getHeaders().getFirst("token");
        log.info("管理员登录验证开始，token：{}", token);

        // 验证token
        if (token == null || token.isEmpty()) {
            log.warn("管理员token为空，请求被拦截");
            return unauthorizedResponse(exchange, "管理员token为空");
        }

        if (!JwtUtil.validate(token)) {
            log.warn("管理员token校验不通过，请求被拦截");
            return unauthorizedResponse(exchange, "管理员token无效或已过期");
        }

        // 解析token，获取管理员信息
        try {
            JSONObject loginAdmin = JwtUtil.getJSONObject(token);
            log.info("当前管理员登录用户：{}", loginAdmin);
            AdminLoginResp admin = JSONUtil.toBean(loginAdmin, AdminLoginResp.class);

            // 将管理员信息添加到请求头，传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Admin-Id", String.valueOf(admin.getId()))
                    .header("X-Admin-Name", admin.getLoginName() != null ? admin.getLoginName() : "")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("解析管理员token失败", e);
            return unauthorizedResponse(exchange, "管理员token解析失败");
        }
    }

    /**
     * 判断是否为排除路径
     */
    private boolean isExcludePath(String path) {
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回未授权响应
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
        // 设置过滤器执行顺序，数字越小优先级越高
        return -100;
    }
}
