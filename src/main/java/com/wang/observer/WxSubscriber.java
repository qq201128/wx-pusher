package com.wang.observer;

import com.wang.domain.IdentityInfo;
import com.wang.enums.WxTemplateType;
import com.wang.strategy.WxTemplateContext;
import com.wang.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

import java.util.Objects;

/**
 * 微信订阅号订阅者
 * 重写一下hash和equals，身份信息相同的subscriber默认是一个订阅者
 * 本类由于需要不同订阅者信息，无法直接使用Spring托管，获取Spring注入对象使用SpringUtils中的方法
 */
@Slf4j
public class WxSubscriber implements Subscriber {
    /**
     * 身份信息
     */
    private final IdentityInfo identityInfo;

    public WxSubscriber(IdentityInfo identityInfo) {
        this.identityInfo = identityInfo;
    }

    @Override
    public void update(WxTemplateType wxTemplateType) {
        try {
            WxMpService wxMpService = new WxMpServiceImpl();
            // 配置基本信息
            WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
            wxMpDefaultConfig.setAppId(identityInfo.getAppId());
            wxMpDefaultConfig.setSecret(identityInfo.getAppSecret());
            // 设置基本信息
            wxMpService.setWxMpConfigStorage(wxMpDefaultConfig);
            // 配置模板信息
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            // 发送的模板信息
            wxMpTemplateMessage.setTemplateId(wxTemplateType.getTemplateId());
            // 接收人
            wxMpTemplateMessage.setToUser(identityInfo.getOpenId());
            // 设置详情跳转url
            String detailUrl = "https://www.baidu.com";
            wxMpTemplateMessage.setUrl(detailUrl);
            // 使用Spring工具类获取Bean
            WxTemplateContext wxTemplateContext = SpringUtils.getBean(WxTemplateContext.class);
            // 调用不同的模板策略
            wxTemplateContext.execute(wxTemplateType, wxMpTemplateMessage, identityInfo);
            // 打印模板内容
            System.out.printf(">>> 模板内容：%s%n", wxMpTemplateMessage.toJson());
            // 发送
            wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        } catch (Exception e) {
            log.error(">>> 给[{}]推送{}策略失败", identityInfo.getOpenId(), wxTemplateType.getTemplateDescription(), e);
        }
    }


    @Override
    public String toString() {
        return "WxSubscriber{" +
                "identityInfo=" + identityInfo +
                '}';
    }

    @Override
    public IdentityInfo getIdentityInfo() {
        return identityInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WxSubscriber that = (WxSubscriber) o;
        return Objects.equals(identityInfo, that.identityInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityInfo);
    }
}
