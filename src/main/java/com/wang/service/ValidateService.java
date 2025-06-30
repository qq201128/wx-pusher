package com.wang.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *  验证接口定义
 */
public interface ValidateService {
    String checkToken(HttpServletRequest request, HttpServletResponse response);
}
