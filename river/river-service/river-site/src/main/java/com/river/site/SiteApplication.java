package com.river.site;

import com.river.common.security.annotation.EnableRiverResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * river 站点管理
 * @author 17822
 */
@SpringBootApplication(scanBasePackages = "com.river")
//服务发现
@EnableDiscoveryClient
//springSecurity 资源服务
@EnableRiverResourceServer
@EnableFeignClients(basePackages="com.river")
public class SiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SiteApplication.class,args);
    }

}
