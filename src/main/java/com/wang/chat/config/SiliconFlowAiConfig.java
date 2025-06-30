package com.wang.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix ="siliconflow")
@Data
public class SiliconFlowAiConfig {
    private  String accessKeyID ;
    private String model;
    private  String chatCompletionUrl;
}
