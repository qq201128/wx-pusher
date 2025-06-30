package com.wang.strategy;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;



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
