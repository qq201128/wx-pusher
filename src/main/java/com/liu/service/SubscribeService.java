package com.liu.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  订阅相关服务接口
 */
public interface SubscribeService {
    void checkToken(HttpServletRequest request, HttpServletResponse response);
}
