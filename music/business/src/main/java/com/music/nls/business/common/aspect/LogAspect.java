package com.music.nls.business.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 定义一个切点
     */
    @Pointcut("execution(public * com.music..*Controller.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        //log.info("前置通知");
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        log.info("--------------环绕通知开始--------------");
        Long startTime = System.currentTimeMillis();//记录开始时间

        //开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = proceedingJoinPoint.getSignature();
        String name = signature.getName();

        //打印请求日志
        log.info("请求地址: {} {}", request.getRequestURI(), request.getMethod());
        log.info("类名方法: {}.{}", signature.getDeclaringTypeName(), name);
        log.info("远程地址: {}", request.getRemoteAddr());

        //答应请求参数
        Object[] args = proceedingJoinPoint.getArgs();
        log.info("请求参数: {}", args);

        //排除特殊类型的参数，如文件类型
        Object[] arguments = new Object[args.length];
        int count = 0;
        for (Object arg : args) {
            if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile || arg instanceof List<?>) {
                continue;
            }
            arguments[count++] = arg;
        }

        //排除字段，敏感字段或太长的字段不显示：身份证，手机号，邮箱，密码等
        String[] excludeProperties = {"password", "idCard"};
        PropertyPreFilters preFilter = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter = preFilter.addFilter();
        excludeFilter.addExcludes(excludeProperties);
        log.info("请求参数: {}", JSONObject.toJSONString(arguments, excludeFilter));

        Object result = proceedingJoinPoint.proceed();//执行目标方法

        //log.info("返回结果: {}", JSONObject.toJSONString(result, excludeFilter));

        // 修改 ExcludeFilter，增加 ByteArrayResource 类型到过滤器中
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            Object body = responseEntity.getBody();
            if (body instanceof ByteArrayResource) {
                log.info("返回结果: 资源类型不支持序列化");
            }
        } else {
            log.info("返回结果: {}", JSONObject.toJSONString(result, excludeFilter));
        }

        log.info("--------------环绕通知结束 耗时: {} ms--------------", System.currentTimeMillis() - startTime);
        return result;
    }

    @After("pointcut()")
    public void doAfter(JoinPoint joinPoint) {
        //log.info("后置通知");
    }
}
