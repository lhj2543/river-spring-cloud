
package com.river.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author river
 * 网关应用
 */

//@SpringCloudApplication
@SpringBootApplication(scanBasePackages = "com.river") //scanBasePackages：扫描包路径
@EnableDiscoveryClient //服务发现
@EnableCircuitBreaker
@EnableFeignClients(basePackages="com.river")
public class authApplication {

	public static void main(String[] args) {
		SpringApplication.run(authApplication.class, args);
	}

}
