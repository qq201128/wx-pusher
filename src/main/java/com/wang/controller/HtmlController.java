package com.wang.controller;

import com.wang.domain.InformationHistory;
import com.wang.service.HtmlService;
import com.wang.service.SubscribeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 控制html内容
 */
@RestController
@RequiredArgsConstructor
public class HtmlController {
   private final HtmlService htmlService;

    @PostMapping("/getHtml")
    public InformationHistory getHtml() {

        return htmlService.getHtml();
    }
}
