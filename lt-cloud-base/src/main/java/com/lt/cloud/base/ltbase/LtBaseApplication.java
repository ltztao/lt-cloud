package com.lt.cloud.base.ltbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LtBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LtBaseApplication.class, args);
    }

}
