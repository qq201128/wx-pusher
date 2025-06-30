package com.wang.strategy.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.common.WxConstants;
import com.wang.domain.IdentityInfo;
import com.wang.enums.WxMessageType;
import com.wang.observer.WxPublisher;
import com.wang.observer.WxSubscriber;
import com.wang.mapper.IdentityInfoMapper;
import com.wang.strategy.WxEventStrategy;
import com.wang.util.WxOpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



import java.util.Map;

/**
 *  订阅活动对应策略
 */
@Service("subscribe")
@Slf4j
public class SubscribeStrategy implements WxEventStrategy {
    @Resource
    private WxPublisher wxPublisher;

    @Resource
    private IdentityInfoMapper identityInfoMapper;

    @Override
    public void execute(Map<String, String> requestMap, HttpServletResponse response) throws Exception {
        log.info(">>> 事件类型：subscribe");
        // 获取订阅者基本信息
        WxSubscriber wxSubscriber = WxOpUtils.getWxSubscriber(requestMap);
        log.info(">>> 新用户订阅！");
        // 加入日常推送列表
        wxPublisher.attach(wxSubscriber);
        // 用户关注后返回信息
        String returnContent = "感谢关注！";
        // 发送方账号
        String openId = requestMap.get("FromUserName");
        // 公众号
        String publicId = requestMap.get("ToUserName");
        WxOpUtils.returnMessages(response, WxMessageType.TEXT.getType(), openId, publicId, returnContent);


        IdentityInfo infoFromDataBase = identityInfoMapper.selectOne(Wrappers.<IdentityInfo>query().lambda()
                .eq(IdentityInfo::getAppId, WxConstants.APP_ID)
                .eq(IdentityInfo::getAppSecret, WxConstants.APP_SECRET)
                .eq(IdentityInfo::getOpenId, openId));

        // 如果没有该订阅者信息则更新，有该订阅者信息则跳过（数据库中的信息可能包括更精确的经纬度信息）
        if (infoFromDataBase != null) {
            return;
        }
        IdentityInfo identityInfo = new IdentityInfo();
        identityInfo.setAppId(WxConstants.APP_ID);
        identityInfo.setAppSecret(WxConstants.APP_SECRET);
        identityInfo.setOpenId(openId);
        identityInfo.setPublicId(publicId);
        identityInfoMapper.insert(identityInfo);
    }
}
