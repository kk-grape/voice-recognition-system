package com.music.order;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.music.order", "com.music.common", "com.music.api"})
@EnableDiscoveryClient
// 扫描 api 模块中的 Feign 接口
@EnableFeignClients(basePackages = "com.music.api")
@MapperScan("com.music.order.mapper")
public class OrderServiceApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(OrderServiceApplication.class, args).getEnvironment();
        String port = environment.getProperty("server.port");
        String appName = environment.getProperty("spring.application.name", "OrderService");
        
        log.info("\u001B[32m【{}】启动成功！\u001B[0m", appName);
        log.info("\u001B[36m服务运行在端口：\u001B[33m{}\u001B[0m", port);
    }

}
