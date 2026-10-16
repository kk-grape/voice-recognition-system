package com.music.nls.business.common.config;

import com.music.nls.business.admin.interceptor.AdminLoginInterceptor;
import com.music.nls.business.common.interceptor.LogInterceptor;
import com.music.nls.business.member.interceptor.WebLoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private LogInterceptor logInterceptor;

    @Resource
    private WebLoginInterceptor webLoginInterceptor;

    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);

        // 路径不要包含context-path
        registry.addInterceptor(webLoginInterceptor)
                .addPathPatterns("/web/**")
                .excludePathPatterns(
                        "/web/kaptcha/image-code/*",
                        "/web/member/login",
                        "/web/member/register",
                        "/web/member/reset",
                        "/web/sms-code/send-for-register",
                        "/web/sms-code/send-for-reset",
                        "/web/fileformat/download-batch"
                );

        // 路径不要包含context-path
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/kaptcha/image-code/*",
                        "/admin/user/login",
                        "/admin/report/**"
                );
    }
}
