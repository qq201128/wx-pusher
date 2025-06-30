package com.wang.controller;

import com.wang.service.ValidateService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


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
