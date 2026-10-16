package com.music.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(GatewayApplication.class, args).getEnvironment();
        String port = environment.getProperty("server.port");
        String appName = environment.getProperty("spring.application.name", "Gateway");
        
        log.info("\u001B[32m【{}】启动成功！\u001B[0m", appName);
        log.info("\u001B[36m服务运行在端口：\u001B[33m{}\u001B[0m", port);
    }

}
