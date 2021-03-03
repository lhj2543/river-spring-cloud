package com.river.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author river
 * 网关应用
 */

//@SpringCloudApplication
@SpringBootApplication(scanBasePackages = "com.river") //scanBasePackages：扫描包路径
@EnableDiscoveryClient //服务发现
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
