package com.music.common.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.music.common.context.LoginAdminContext;
import com.music.common.resp.AdminLoginResp;
import com.music.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if(request.getMethod().toUpperCase().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURL().toString();
        log.info("接口登录拦截，path：{}", path);

        // 优先使用Gateway传递的管理员信息（Gateway已验证过token）
        String adminId = request.getHeader("X-Admin-Id");
        String adminName = request.getHeader("X-Admin-Name");
        String token = request.getHeader("token");

        if (adminId != null && !adminId.isEmpty()) {
            // Gateway已验证过token，直接使用传递的管理员信息
            log.info("从Gateway获取管理员信息，adminId：{}，adminName：{}", adminId, adminName);
            try {
                AdminLoginResp admin = new AdminLoginResp();
                admin.setId(Long.parseLong(adminId));
                admin.setLoginName(adminName);
                if (token != null) {
                    admin.setToken(token);
                }

                log.info("<保存管理员信息到上下文>");
                LoginAdminContext.setAdmin(admin);
                return true;
            } catch (Exception e) {
                log.warn("解析Gateway传递的管理员信息失败，回退到token验证", e);
                // 解析失败，回退到token验证
            }
        }
        
        // 如果没有Gateway传递的信息（可能绕过Gateway直接访问），进行token验证
        log.info("未从Gateway获取管理员信息，进行本地token验证");
        if (token == null || token.isEmpty()) {
            log.info( "token为空，请求被拦截" );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        } else if (!JwtUtil.validate(token)) {
            log.info( "token校验不通过，请求被拦截" );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        } else {
            log.info("获取控台管理员登录token：{}", token);
            JSONObject loginAdmin = JwtUtil.getJSONObject(token);
            log.info("当前控台登录管理员：{}", loginAdmin);
            AdminLoginResp admin = JSONUtil.toBean(loginAdmin, AdminLoginResp.class);
            admin.setToken(token);
            LoginAdminContext.setAdmin(admin);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoginAdminContext.removeAdmin();
    }
}
