package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.domain.IdentityInfo;
import com.wang.strategy.WxTemplateStrategy;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 特殊下午模板
 */
@Service(WxTemplateConstants.SPECIAL_AFTERNOON)
public class SpecialAfternoonStrategy implements WxTemplateStrategy {
    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {

    }
}
