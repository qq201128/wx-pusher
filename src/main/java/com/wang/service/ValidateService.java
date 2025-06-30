package com.wang.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  验证接口定义
 */
public interface ValidateService {
    String checkToken(HttpServletRequest request, HttpServletResponse response);
}
