package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.dto.IdentityInfo;
import com.wang.strategy.WxTemplateStrategy;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

/**
 * 特殊晚安策略
 */
@Service(WxTemplateConstants.SPECIAL_NIGHT)
public class SpecialNightStrategy implements WxTemplateStrategy {
    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        String sayUrl = "http://api.tianapi.com/lzmy/index?key=" + WxConstants.TX_AK;
        String sayStr = HttpUtil.get(sayUrl);
        String sayList = JSONObject.parseObject(sayStr).get("newslist").toString();
        String saying = JSONArray.parseArray(sayList).getJSONObject(0).get("saying").toString();
        String title = JSONArray.parseArray(sayList).getJSONObject(0).get("source").toString();
        wxMpTemplateMessage.addData(new WxMpTemplateData("location", identityInfo.getAddress(), "#9370DB"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("saying", saying, "#6699FF"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("title", "《" + title + "》", "#CCFF99"));
    }
}
