package com.boot.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EntityScan(basePackages = {"com.boot.services.model", "com.boot.user.model"})
public class SpringUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringUserServiceApplication.class, args);
    }
}
