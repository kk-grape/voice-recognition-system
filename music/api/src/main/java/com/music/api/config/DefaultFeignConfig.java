package com.music.api.config;

import com.music.common.context.LoginMemberContext;
import com.music.common.context.LoginAdminContext;
import com.music.common.resp.MemberLoginResp;
import com.music.common.resp.AdminLoginResp;
import feign.*;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * Feign 默认配置：
 * - 在服务间通过 Feign 远程调用时，自动从 ThreadLocal 中获取当前登录用户/会员信息
 * - 并透传到下游服务的请求头中，供下游拦截器再次解析设置上下文
 * - 记录远程调用的请求和响应日志，便于排查问题
 *
 * 注意：需在 @FeignClient 上通过 configuration = DefaultFeignConfig.class 显式开启，
 * 不作为全局配置自动生效。
 */
@Slf4j
public class DefaultFeignConfig {

    @Bean
    public RequestInterceptor userContextFeignInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 优先处理后台管理员登录用户
                AdminLoginResp admin = LoginAdminContext.getAdmin();
                if (admin != null) {
                    if (admin.getId() != null) {
                        template.header("X-Admin-Id", String.valueOf(admin.getId()));
                    }
                    if (admin.getLoginName() != null) {
                        template.header("X-Admin-Name", admin.getLoginName());
                    }
                    if (admin.getToken() != null) {
                        template.header("token", admin.getToken());
                    }
                    return;
                }

                // 其次处理前台会员登录用户
                MemberLoginResp member = LoginMemberContext.getMember();
                if (member != null) {
                    if (member.getId() != null) {
                        template.header("X-Member-Id", String.valueOf(member.getId()));
                    }
                    if (member.getName() != null) {
                        template.header("X-Member-Name", member.getName());
                    }
                    if (member.getToken() != null) {
                        template.header("token", member.getToken());
                    }
                }
            }
        };
    }

    /**
     * Seata 分布式事务 XID 透传拦截器
     *
     * 将当前线程中的全局事务 XID 写入到 Feign 请求头中，
     * 下游服务（如 order-service）收到后，Seata Web 拦截器会自动绑定到 RootContext，
     * 从而让其本地数据库操作作为一个分支事务加入同一个全局事务。
     */
    @Bean
    public RequestInterceptor seataFeignInterceptor() {
        return template -> {
            String xid = RootContext.getXID();
            if (xid != null) {
                template.header(RootContext.KEY_XID, xid);
            }
        };
    }

    /**
     * Feign 日志记录器
     * 记录远程调用的基本信息：请求方法、URL、响应状态码和耗时
     * 
     * 注意：Bean 名称必须是 "logger"，Feign 会自动查找这个名称的 Bean
     */
    @Bean
    public Logger logger() {
        return new FeignRequestLogger();
    }

    /**
     * 设置 Feign 日志级别为 FULL，确保所有请求和响应都被记录
     * 
     * 注意：Bean 名称必须是 "feignLoggerLevel"，Feign 会自动查找这个名称的 Bean
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 自定义 Feign 日志记录器 - 简化版
     * 
     * 注意：需要在 application.yml 中配置日志级别：
     * logging:
     * level:
     * com.music.api.config.DefaultFeignConfig$FeignRequestLogger: info
     */
    public static class FeignRequestLogger extends Logger {

        // 使用固定的 logger 名称，方便在配置文件中设置日志级别
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("FeignRequestLogger");

        @Override
        protected void log(String configKey, String format, Object... args) {
            // 使用 SLF4J 记录日志
            if (logger.isDebugEnabled()) {
                logger.debug(String.format(methodTag(configKey) + format, args));
            }
        }

        @Override
        protected void logRequest(String configKey, Level logLevel, Request request) {
            // 直接输出，不检查级别，因为 Feign 的 Level 已经控制是否调用此方法
            logger.info("Feign 请求: {} {} -> {}", request.method(), request.url(), configKey);
        }

        @Override
        protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
                throws IOException {
            // 直接输出，不检查级别，因为 Feign 的 Level 已经控制是否调用此方法
            logger.info("Feign 响应: {} -> {} 状态码: {} 耗时: {}ms",
                    configKey, response.request().url(), response.status(), elapsedTime);
            return response;
        }
    }
}
