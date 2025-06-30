package com.wang.controller;

import com.wang.enums.WxTemplateType;
import com.wang.observer.WxPublisher;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务调度组件
 */
@Component
@Slf4j
public class TimedTaskJob {
    @Resource
    private WxPublisher wxPublisher;

    /**
     * 给特殊的人发早安（SPECIAL_MORNING模板）
     */
    @Scheduled(cron = "0 * * * * ?")
    public void executeSpecialMorningTask() {
        wxPublisher.inform(WxTemplateType.SPECIAL_MORNING);
    }

    /**
     * 给除了特殊的人以外的人发早安，（COMMON_MORNING模板）
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void executeCommonMorningTask() {
        wxPublisher.inform(WxTemplateType.COMMON_MORNING);
    }

    @Scheduled(cron = "0 30 16 * * ?")
    public void executeSpecialAfternoonTask() {
        wxPublisher.inform(WxTemplateType.SPECIAL_AFTERNOON);
    }

    @Scheduled(cron = "0 30 22 * * ?")
    public void executeSpecialNightTask() {
        wxPublisher.inform(WxTemplateType.SPECIAL_NIGHT);
    }

    /**
     * 一个小时获取一次accessToken
     */
    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 0)
    public void acquireAccessToken() {
        // 这里建议直接调用 WxOpUtils.getAccessToken()，如需日志可保留
        // WxConstants.accessToken = WxOpUtils.getAccessToken();
        log.info(">>> update access_token at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(new Date()) + " --------> " + "[token omitted]");
    }
} 