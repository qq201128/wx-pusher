package com.liu.strategy;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 *  容器类，用来选择使用哪种策略
 */
@Service
public class WxEventContext {
    @Resource
    private Map<String, WxEventStrategy> strategyMap;

    public void execute(String strategy, Map<String, String> requestMap, HttpServletResponse response) throws Exception {
        strategyMap.get(strategy).execute(requestMap, response);
    }
}
