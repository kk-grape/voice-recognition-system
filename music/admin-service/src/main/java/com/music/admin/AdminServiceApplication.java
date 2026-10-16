package com.music.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication(scanBasePackages = { "com.music.admin", "com.music.common", "com.music.api" })
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.music.api")
@MapperScan("com.music.admin.mapper")
public class AdminServiceApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(AdminServiceApplication.class, args).getEnvironment();
        String port = environment.getProperty("server.port");
        String appName = environment.getProperty("spring.application.name", "AdminService");
        
        log.info("\u001B[32m【{}】启动成功！\u001B[0m", appName);
        log.info("\u001B[36m服务运行在端口：\u001B[33m{}\u001B[0m", port);
    }

}
