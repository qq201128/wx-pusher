package com.wang.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  订阅相关服务接口
 */
public interface SubscribeService {
    void checkToken(HttpServletRequest request, HttpServletResponse response);
}
