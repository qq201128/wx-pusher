package com.liu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 */
@SpringBootApplication
@EnableScheduling
public class WxPusherApplication {
    public static void main(String[] args) {
        SpringApplication.run(WxPusherApplication.class, args);
    }
}
