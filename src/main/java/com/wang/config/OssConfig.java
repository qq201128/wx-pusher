package com.wang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    // 阿里云OSS
    private String endpoint;
    // 阿里云账号AccessKey拥有所有API的访问权限
    private String accessKeyId;
    // 阿里云账号AccessKey的SecretKey
    private String accessKeySecret;
    // 阿里云OSS存储空间名称
    private String bucketName;
}
