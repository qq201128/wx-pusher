package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.chat.service.impl.SiliconFlowAIServiceImpl;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.domain.DistrictInfo;
import com.wang.domain.IdentityInfo;
import com.wang.domain.InformationHistory;
import com.wang.exception.WxException;
import com.wang.mapper.DistrictInfoMapper;
import com.wang.mapper.InformationHistoryMapper;
import com.wang.strategy.WxTemplateStrategy;
import com.wang.util.WxOpUtils;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  特殊早安推送策略
 */
@Service(WxTemplateConstants.SPECIAL_MORNING)
@RequiredArgsConstructor
public class SpecialMorningStrategy implements WxTemplateStrategy {

    private final SiliconFlowAIServiceImpl siliconFlowAIService;

    private final DistrictInfoMapper districtInfoMapper;
    private final InformationHistoryMapper informationHistoryMapper;
    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        Integer districtCode = getDistrictCode(identityInfo);
        // 获取天气的url
        String weatherUrl = "https://api.map.baidu.com/weather/v1/?district_id=" + districtCode + "&data_type=all&ak=" + WxConstants.BAI_DU_AK;
        // 天气信息json格式
        String weatherStr = HttpUtil.get(weatherUrl);
        JSONObject result = JSONObject.parseObject(JSONObject.parseObject(weatherStr).get("result").toString());
        // 实时天气
        JSONObject now = JSONObject.parseObject(result.get("now").toString());
        // 今日天气
        JSONObject today = JSONArray.parseArray(result.get("forecasts").toString()).getJSONObject(0);
        // 明日天气
        JSONObject tomorrow = JSONArray.parseArray(result.get("forecasts").toString()).getJSONObject(1);

        //每日打工语录
        String workUrl = "https://apis.tianapi.com/dgryl/index?key=" + WxConstants.TX_AK;
        String workStr = HttpUtil.get(workUrl);
        JSONObject workJson = JSONObject.parseObject(workStr);
        JSONObject workObject = workJson.getJSONObject("result");
        // 打工语录句子
        String work = workObject != null ? workObject.getString("content") : "";

        /*// 每日英语
        String dailyEnglishUrl = "http://api.tianapi.com/everyday/index?key=" + WxConstants.TX_AK;
        String dailyEnglishStr = HttpUtil.get(dailyEnglishUrl);
        JSONObject dailyEnglishObject = JSONArray.parseArray(JSONObject.parseObject(dailyEnglishStr).get("newslist").toString()).getJSONObject(0);
        // 英文句子
        String english = dailyEnglishObject.get("content").toString();
        // 20230505更新，wx平台最新规范[https://developers.weixin.qq.com/community/develop/doc/000a2ae286cdc0f41a8face4c51801]
        // 每个模板块最多只能填充20个字符，需要对超长内容切割
        String english1 = english.substring(0, Math.min(english.length(), 20));
        String english2 = null;
        if (english.length() > 20) {
            english2 = english.substring(20);
        }
        // 中文翻译
        String chinese = dailyEnglishObject.get("note").toString();
        String chinese1 = chinese.substring(0, Math.min(chinese.length(), 20));
        String chinese2 = null;
        if (chinese.length() > 20) {
            chinese2 = chinese.substring(20);
        }*/
        wxMpTemplateMessage.addData(new WxMpTemplateData("location", identityInfo.getAddress()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("now_temp", now.get("temp").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("now_weather", now.get("text").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("now_wind_dir", now.get("wind_dir").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("now_wind_class", now.get("wind_class").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("now_rh", now.get("rh").toString()));
        String todayWeatherDay = today.get("text_day").toString();
        String todayWeatherNight = today.get("text_night").toString();
        if (todayWeatherDay.equals(todayWeatherNight)) {
            wxMpTemplateMessage.addData(new WxMpTemplateData("today_weather", todayWeatherDay));
        } else {
            wxMpTemplateMessage.addData(new WxMpTemplateData("today_weather", todayWeatherDay + "转" + todayWeatherNight));
        }
        wxMpTemplateMessage.addData(new WxMpTemplateData("today_high", today.get("high").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("today_low", today.get("low").toString()));
        String tomorrowWeatherDay = tomorrow.get("text_day").toString();
        String tomorrowWeatherNight = tomorrow.get("text_night").toString();
        if (tomorrowWeatherDay.equals(tomorrowWeatherNight)) {
            wxMpTemplateMessage.addData(new WxMpTemplateData("tomorrow_weather", tomorrowWeatherDay));
        } else {
            wxMpTemplateMessage.addData(new WxMpTemplateData("tomorrow_weather", tomorrowWeatherDay + "转" + tomorrowWeatherNight));
        }
        wxMpTemplateMessage.addData(new WxMpTemplateData("tomorrow_high", tomorrow.get("high").toString()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("tomorrow_low", tomorrow.get("low").toString()));
        // 相识天数，可以修改为恋爱天数，或者其他纪念意义天数
        Long meetDays = WxOpUtils.countDays(WxConstants.LOVE_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("love_days", String.valueOf(meetDays)));
        wxMpTemplateMessage.addData(new WxMpTemplateData("work", work));

        String htmlContent = siliconFlowAIService.chatToMorning(wxMpTemplateMessage.getData().toString() + "我们的恋爱时间为"+WxConstants.LOVE_DATE);
//        System.out.println(htmlContent);
        wxMpTemplateMessage.setUrl("http://3e3e0af0.r36.cpolar.top");

        InformationHistory informationHistory = new InformationHistory();
        informationHistory.setOpenId(identityInfo.getOpenId());
        informationHistory.setInformation(htmlContent);
        informationHistory.setCreateDate(new Date());
        informationHistoryMapper.insert(informationHistory);

//        wxMpTemplateMessage.addData(new WxMpTemplateData("daily_english_en1", english1, "#FFCCFF"));
//        wxMpTemplateMessage.addData(new WxMpTemplateData("daily_english_en2", english2, "#FFCCFF"));
//        wxMpTemplateMessage.addData(new WxMpTemplateData("daily_english_cn1", chinese1, "#CCCCFF"));
//        wxMpTemplateMessage.addData(new WxMpTemplateData("daily_english_cn2", chinese2, "#CCCCFF"));
    }
    public Integer getDistrictCode(IdentityInfo identityInfo) {
        if (identityInfo == null) {
            throw new WxException("identityInfo 为空");
        }
        String district = identityInfo.getDistrict();
        String city = identityInfo.getCity();
        if (district == null || city == null) {
            throw new WxException("district 或 city 为空，district=" + district + ", city=" + city);
        }
        try {
            char suffix = district.charAt(district.length() - 1);
            if (suffix == '区' || suffix == '县') {
                district = district.substring(0, district.length() - 1);
            }
            if (city.charAt(city.length() - 1) == '市') {
                city = city.substring(0, city.length() - 1);
            }
            DistrictInfo districtInfo = districtInfoMapper.selectOne(Wrappers.<DistrictInfo>query().lambda()
                    .like(DistrictInfo::getCity, city)
                    .like(DistrictInfo::getDistrict, district));
            if (districtInfo == null || districtInfo.getDistrictCode() == null) {
                throw new WxException("未找到地区编码，city=" + city + ", district=" + district);
            }
            Integer districtCode = districtInfo.getDistrictCode();
//            Integer districtCode = SpringUtils.getBean(DistrictInfo.class).getDistrictCode();

            return districtCode;
        } catch (Exception e) {
            throw new WxException("获取地区编码错误，city=" + city + ", district=" + district + "，请检查是否开启允许地理位置访问！");
        }
    }

}
