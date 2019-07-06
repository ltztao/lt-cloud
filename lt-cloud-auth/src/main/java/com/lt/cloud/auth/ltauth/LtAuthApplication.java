package com.lt.cloud.auth.ltauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LtAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LtAuthApplication.class, args);
    }

}
