package com.wang.strategy.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.common.WxConstants;
import com.wang.domain.IdentityInfo;
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
 *  取消订阅活动对应策略
 */
@Service("unsubscribe")
@Slf4j
public class UnsubscribeStrategy implements WxEventStrategy {
    @Resource
    private WxPublisher wxPublisher;

    @Resource
    private IdentityInfoMapper identityInfoMapper;

    @Override
    public void execute(Map<String, String> requestMap, HttpServletResponse response) throws Exception {
        log.info(">>> 事件类型：unsubscribe");
        // 获取取消订阅者信息
        WxSubscriber wxSubscriber = WxOpUtils.getWxSubscriber(requestMap);
        log.info(">>> 用户取消订阅！");
        wxPublisher.detach(wxSubscriber);
        // 数据库中状态修改
        String openId = requestMap.get("FromUserName");


        // 修改status为1
        IdentityInfo identityInfo = identityInfoMapper.selectOne(Wrappers.<IdentityInfo>query().lambda()
                .eq(IdentityInfo::getAppId, WxConstants.APP_ID)
                .eq(IdentityInfo::getAppSecret, WxConstants.APP_SECRET)
                .eq(IdentityInfo::getOpenId, openId));
        if (identityInfo != null ) {

            identityInfo.setStatus(1);
            identityInfoMapper.update(identityInfo, Wrappers.<IdentityInfo>query().lambda()
                    .eq(IdentityInfo::getAppId, identityInfo.getAppId())
                    .eq(IdentityInfo::getAppSecret, identityInfo.getAppSecret())
                    .eq(IdentityInfo::getOpenId, identityInfo.getOpenId()));
        };
    }
}
