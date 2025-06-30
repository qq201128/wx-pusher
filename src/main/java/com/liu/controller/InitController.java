package com.liu.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.liu.common.WxConstants;
import com.liu.dto.IdentityInfo;
import com.liu.dto.IdentityInfoKey;
import com.liu.observer.WxPublisher;
import com.liu.observer.WxSubscriber;
import com.liu.repository.IdentityInfoRepository;
import com.liu.util.WxOpUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 初始化需要获取的一些数据
 */
@RestController
public class InitController {
    @Resource
    private WxPublisher wxPublisher;

    @Resource
    private IdentityInfoRepository identityInfoRepository;

    /**
     * 初始化从远端拉取关注列表，比从数据库里拿更好
     */
    @PostConstruct
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
            IdentityInfoKey identityInfoKey = new IdentityInfoKey(WxConstants.APP_ID, WxConstants.APP_SECRET, openId);
            IdentityInfo infoFromDataBase = identityInfoRepository.findById(identityInfoKey).orElse(null);
            WxSubscriber wxSubscriber;
            if (infoFromDataBase == null) {
                identityInfoRepository.save(identityInfo);
                wxSubscriber = new WxSubscriber(identityInfo);
            } else {
                wxSubscriber = new WxSubscriber(infoFromDataBase);
            }
            wxPublisher.attach(wxSubscriber);
        }
    }
}
