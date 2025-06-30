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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 特殊下午模板
 */
@Service(WxTemplateConstants.SPECIAL_AFTERNOON)
public class SpecialAfternoonStrategy implements WxTemplateStrategy {
    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        String poemUrl = "http://api.tianapi.com/verse/index?key=" + WxConstants.TX_AK;
        String poemStr = HttpUtil.get(poemUrl);
        String poemList = JSONObject.parseObject(poemStr).get("newslist").toString();
        String poem = JSONArray.parseArray(poemList).getJSONObject(0).get("content").toString();
        String title = JSONArray.parseArray(poemList).getJSONObject(0).get("source").toString();
        wxMpTemplateMessage.addData(new WxMpTemplateData("location", identityInfo.getAddress(), "#9370DB"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("time", time, "#7CFC00"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("poem", poem, "#FFA500"));
        wxMpTemplateMessage.addData(new WxMpTemplateData("title", "《" + title + "》", "#FF6347"));
    }
}
