package com.music.common.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.music.common.context.LoginMemberContext;
import com.music.common.resp.MemberLoginResp;
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
public class MemberLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if(request.getMethod().toUpperCase().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURL().toString();
        log.info("接口登录拦截，path：{}", path);

        // 优先使用Gateway传递的会员信息（Gateway已验证过token）
        String memberId = request.getHeader("X-Member-Id");
        String memberName = request.getHeader("X-Member-Name");
        String token = request.getHeader("token");
        
        if (memberId != null && !memberId.isEmpty()) {
            // Gateway已验证过token，直接使用传递的会员信息
            log.info("从Gateway获取会员信息，memberId：{}，memberName：{}", memberId, memberName);
            try {
                MemberLoginResp member = new MemberLoginResp();
                member.setId(Long.parseLong(memberId));
                member.setName(memberName);
                if (token != null) {
                    member.setToken(token);
                }

                log.info("<当前服务保存会员信息到上下文>");
                LoginMemberContext.setMember(member);
                return true;
            } catch (Exception e) {
                log.warn("解析Gateway传递的会员信息失败，回退到token验证", e);
                // 解析失败，回退到token验证
            }
        }
        
        // 如果没有Gateway传递的信息（可能绕过Gateway直接访问），进行token验证
        log.info("未从Gateway获取会员信息，进行本地token验证");
        if (token == null || token.isEmpty()) {
            log.info( "token为空，请求被拦截" );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        } else if (!JwtUtil.validate(token)) {
            log.info( "token校验不通过，请求被拦截" );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        } else {
            log.info("获取会员登录token：{}", token);
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            log.info("当前登录会员：{}", loginMember);
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            member.setToken(token);
            LoginMemberContext.setMember(member);
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LoginMemberContext.removeMember();
    }
}
