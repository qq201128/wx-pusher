package com.wang.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.common.WxConstants;
import com.wang.domain.IdentityInfo;
import com.wang.observer.WxPublisher;
import com.wang.observer.WxSubscriber;
import com.wang.mapper.IdentityInfoMapper;
import com.wang.util.WxOpUtils;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 初始化需要获取的一些数据
 */
@RestController
public class InitController {
    @Resource
    private WxPublisher wxPublisher;

    @Resource
    private IdentityInfoMapper identityInfoMapper;

    /**
     * 初始化从远端拉取关注列表，比从数据库里拿更好
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        String accessToken = WxOpUtils.getAccessToken();
        // 获取关注者列表
        String openIdUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken;
        String openIdStr = HttpUtil.get(openIdUrl);
        Object data = JSONObject.parseObject(openIdStr).get("data");
        // 没有获取关注者直接return
        if (data == null) {
            return;
        }
        String openIdsStr = JSONObject.parseObject(data.toString()).get("openid").toString();
        List<String> openIdList = JSONArray.parseArray(openIdsStr).toJavaList(String.class);
        for (String openId : openIdList) {
            IdentityInfo identityInfo = new IdentityInfo();
            identityInfo.setAppId(WxConstants.APP_ID);
            identityInfo.setAppSecret(WxConstants.APP_SECRET);
            identityInfo.setOpenId(openId);
            identityInfo.setPublicId(WxConstants.PUBLIC_ID);
            // 查询表中是否有对应主键的身份信息


            IdentityInfo infoFromDataBase = identityInfoMapper.selectOne(Wrappers.<IdentityInfo>query().lambda()
                    .eq(IdentityInfo::getAppId, WxConstants.APP_ID)
                    .eq(IdentityInfo::getAppSecret, WxConstants.APP_SECRET)
                    .eq(IdentityInfo::getOpenId, openId));
            WxSubscriber wxSubscriber;
            if (infoFromDataBase == null) {
                identityInfoMapper.insert(identityInfo);
                wxSubscriber = new WxSubscriber(identityInfo);
            } else {
                wxSubscriber = new WxSubscriber(infoFromDataBase);
            }
            wxPublisher.attach(wxSubscriber);
        }
    }
}
