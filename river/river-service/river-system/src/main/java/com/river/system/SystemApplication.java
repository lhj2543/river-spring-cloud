
package com.river.system;

import com.river.common.security.annotation.EnableRiverResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author river
 * 系统管理
 */
//@SpringCloudApplication
@SpringBootApplication(scanBasePackages = "com.river")
//服务发现
@EnableDiscoveryClient
//springSecurity 资源服务
@EnableRiverResourceServer
@EnableFeignClients(basePackages="com.river")
public class SystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemApplication.class, args);
	}

}
