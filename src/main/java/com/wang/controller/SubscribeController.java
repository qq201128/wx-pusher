package com.wang.controller;

import com.wang.service.SubscribeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 订阅控制器
 */
@RestController
public class SubscribeController {
    @Resource
    private SubscribeService subscribeService;

    @PostMapping("/checkToken")
    public void checkToken(HttpServletRequest request, HttpServletResponse response) {
        subscribeService.checkToken(request, response);
    }
}
