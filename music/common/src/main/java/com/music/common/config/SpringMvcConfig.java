package com.music.common.config;


import com.music.common.interceptor.AdminLoginInterceptor;
import com.music.common.interceptor.LogInterceptor;
import com.music.common.interceptor.MemberLoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private LogInterceptor logInterceptor;

    @Resource
    private MemberLoginInterceptor memberLoginInterceptor;

    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);

        // 路径不要包含context-path
        registry.addInterceptor(memberLoginInterceptor)
                .addPathPatterns("/web/**")
                .excludePathPatterns(
                        "/web/kaptcha/image-code/*",
                        "/web/member/login",
                        "/web/member/register",
                        "/web/member/reset",
                        "/web/sms-code/send-for-register",
                        "/web/sms-code/send-for-reset",
                        "/web/fileformat/download-batch",
                        "/web/filetrans/after-pay-success"  // 服务间调用，不需要用户认证
                );

        // 路径不要包含context-path
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/kaptcha/image-code/*",
                        "/admin/login"
                );
    }
}
