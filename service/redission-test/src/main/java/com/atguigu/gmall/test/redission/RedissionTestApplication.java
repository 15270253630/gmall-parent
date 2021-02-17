package com.atguigu.gmall.test.redission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.alibaba.nacos.registry.NacosAutoServiceRegistration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.atguigu.gmall",exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient

public class RedissionTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedissionTestApplication.class);
    }
}
