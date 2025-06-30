package com.wang.controller;

import com.wang.service.ValidateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  验证类的控制器
 */
@RestController
public class ValidateController {
    @Resource
    private ValidateService validateService;

    @GetMapping("/checkToken")
    public String checkToken(HttpServletRequest request, HttpServletResponse response) {
        return validateService.checkToken(request, response);
    }
}
