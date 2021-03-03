package com.river.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author river
 */
@SpringBootApplication(scanBasePackages = "com.river")
public class RiverSpringAppliction {

    public static void main(String[] args) {
        SpringApplication.run(RiverSpringAppliction.class,args);
    }

}
