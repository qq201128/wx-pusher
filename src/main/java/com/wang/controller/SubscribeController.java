package com.wang.controller;

import com.wang.service.SubscribeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
