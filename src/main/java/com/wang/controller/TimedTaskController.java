package com.wang.controller;

import com.wang.common.WxConstants;
import com.wang.enums.WxTemplateType;
import com.wang.observer.WxPublisher;
import com.wang.util.WxOpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务
 */
@RestController
@Slf4j
public class TimedTaskController {
    @Resource
    private WxPublisher wxPublisher;

    /**
     * 给特殊的人发早安（SPECIAL_MORNING模板）
     */
    @PostMapping("/executeSpecialMorningTask")
    @Scheduled(cron = "0 * * * * ?")
    public void executeSpecialMorningTask() {
        System.out.println("00000000000000000000000");
        wxPublisher.inform(WxTemplateType.SPECIAL_MORNING);
    }

    /**
     * 给除了特殊的人以外的人发早安，（COMMON_MORNING模板）
     */
    @PostMapping("/executeCommonMorningTask")
    @Scheduled(cron = "0 30 7 * * ?")
    public void executeCommonMorningTask() {
        wxPublisher.inform(WxTemplateType.COMMON_MORNING);
    }

    @PostMapping("/executeSpecialAfternoonTask")
    @Scheduled(cron = "0 30 16 * * ?")
    public void executeSpecialAfternoonTask() {
        wxPublisher.inform(WxTemplateType.SPECIAL_AFTERNOON);
    }

    @PostMapping("/executeSpecialNightTask")
    @Scheduled(cron = "0 30 22 * * ?")
    public void executeSpecialNightTask() {
        wxPublisher.inform(WxTemplateType.SPECIAL_NIGHT);
    }


    /**
     * 一个小时获取一次accessToken
     */
    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 0)
    public void acquireAccessToken() {
        WxConstants.accessToken = WxOpUtils.getAccessToken();
        log.info(">>> update access_token at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(new Date()) + " --------> " + WxConstants.accessToken);
    }

}
