package com.wang.strategy;

import com.wang.domain.IdentityInfo;
import com.wang.enums.WxTemplateType;
import jakarta.annotation.Resource;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  微信模板上下文
 */
@Component
public class WxTemplateContext {
    @Resource
    private Map<String, WxTemplateStrategy> strategyMap;

    public void execute(WxTemplateType wxTemplateType, WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        strategyMap.get(wxTemplateType.getTemplateId()).execute(wxMpTemplateMessage, identityInfo);
    }
}
