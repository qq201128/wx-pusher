package com.wang.controller;

import com.wang.domain.InformationHistory;
import com.wang.service.HtmlService;
import com.wang.service.SubscribeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 控制html内容
 */
@RestController
@RequiredArgsConstructor
public class HtmlController {
   private final HtmlService htmlService;

    @GetMapping("/getHtml")
    public InformationHistory getHtml(@RequestParam("openId") String openId) {
        return htmlService.getHtml(openId);
    }
}
