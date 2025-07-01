package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.chat.service.impl.SiliconFlowAIServiceImpl;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.domain.*;
import com.wang.exception.WxException;
import com.wang.mapper.DistrictInfoMapper;
import com.wang.mapper.InformationHistoryMapper;
import com.wang.strategy.WxTemplateStrategy;
import com.wang.util.WxOpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 普通早安消息推送策略
 */
@Service(WxTemplateConstants.COMMON_MORNING)
@Slf4j
@RequiredArgsConstructor
public class CommonMorningStrategy implements WxTemplateStrategy {
    private static final String WORK_URL = "https://apis.tianapi.com/dgryl/index?key=";
    private static final String HOLIDAY_URL = "https://apis.tianapi.com/jiejiari/index?key=";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final DistrictInfoMapper districtInfoMapper;
    private final SiliconFlowAIServiceImpl siliconFlowAIService;
    private final InformationHistoryMapper informationHistoryMapper;

    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        String today = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String work = getWorkQuote();
        HolidayInfo holidayInfo = getHolidayInfo(today);
        NextHolidayInfo nextHolidayInfo = getNextHolidayInfo(today);

        Long meetDays = WxOpUtils.countDays(WxConstants.LOVE_DATE, today);
        wxMpTemplateMessage.addData(new WxMpTemplateData("love_days", String.valueOf(meetDays)));
        wxMpTemplateMessage.addData(new WxMpTemplateData("work", work));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday", holidayInfo.getHolidayInfo()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("days_to_next_holiday", nextHolidayInfo.getDaysToNextHoliday()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("next_holiday_name", nextHolidayInfo.getNextHolidayName()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("next_holiday_date", nextHolidayInfo.getNextHolidayDate()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday_tip", nextHolidayInfo.getNextHolidayTip()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday_rest", nextHolidayInfo.getNextHolidayRest()));

        String htmlContent = siliconFlowAIService.chatToCommonMorning(wxMpTemplateMessage.getData().toString() + "我们的恋爱时间为" + WxConstants.LOVE_DATE);
        wxMpTemplateMessage.setUrl("http://3e3e0af0.r36.cpolar.top?openid=" + identityInfo.getOpenId());
        InformationHistory informationHistory = new InformationHistory();
        informationHistory.setOpenId(identityInfo.getOpenId());
        informationHistory.setInformation(htmlContent);
        informationHistory.setCreateDate(new Date());
        informationHistoryMapper.insert(informationHistory);
    }

    private String getWorkQuote() {
        try {
            String workStr = HttpUtil.get(WORK_URL + WxConstants.TX_AK);
            JSONObject workJson = JSONObject.parseObject(workStr);
            JSONObject workObject = workJson.getJSONObject("result");
            return workObject != null ? workObject.getString("content") : "";
        } catch (Exception e) {
            log.error("获取打工语录失败", e);
            return "";
        }
    }

    private HolidayInfo getHolidayInfo(String today) {
        try {
            String holidayStr = HttpUtil.get(HOLIDAY_URL + WxConstants.TX_AK + "&date=" + today + "&type=0");
            JSONObject holidayJson = JSONObject.parseObject(holidayStr);
            JSONObject result = holidayJson.getJSONObject("result");
            if (result == null){
                return new HolidayInfo("工作日", 0, null, null);
            }
            int daycode = result.getIntValue("daycode");
            String info = result.getString("info");
            String name = result.getString("name");
            String holidayInfo;
            if (daycode == 1) {
                holidayInfo = name != null ? name : info;
            } else if (daycode == 2) {
                holidayInfo = "双休日";
            } else if (daycode == 3) {
                holidayInfo = "调休日";
            } else {
                holidayInfo = "工作日";
            }
            return new HolidayInfo(holidayInfo, daycode, name, info);
        } catch (Exception e) {
            log.error("获取节假日信息失败", e);
            return new HolidayInfo("工作日", 0, null, null);
        }
    }

    private NextHolidayInfo getNextHolidayInfo(String today) {
        try {
            String nextHolidayStr = HttpUtil.get(HOLIDAY_URL + WxConstants.TX_AK + "&date=" + today + "&type=1");
            JSONObject nextHolidayJson = JSONObject.parseObject(nextHolidayStr);
            JSONObject resultObj = nextHolidayJson.getJSONObject("result");
            JSONArray holidayList = resultObj != null ? resultObj.getJSONArray("list") : null;
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            Date now = sdf.parse(today);
            JSONObject nextHolidayItem = null;
            int minDays = Integer.MAX_VALUE;
            String nextHolidayDate = "";
            if (holidayList != null && !holidayList.isEmpty()) {
                for (int i = 0; i < holidayList.size(); i++) {
                    JSONObject holidayItem = holidayList.getJSONObject(i);
                    Object vacationObj = holidayItem.get("vacation");
                    List<String> vacationDates = new ArrayList<>();
                    if (vacationObj instanceof JSONArray) {
                        JSONArray vacationArr = (JSONArray) vacationObj;
                        for (int j = 0; j < vacationArr.size(); j++) {
                            vacationDates.add(vacationArr.getString(j));
                        }
                    } else if (vacationObj instanceof String) {
                        vacationDates.add((String) vacationObj);
                    }
                    for (String vacationDateStr : vacationDates) {
                        Date vacationDate = sdf.parse(vacationDateStr);
                        if (vacationDate.after(now)) {
                            int days = (int) ((vacationDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
                            if (days < minDays) {
                                minDays = days;
                                nextHolidayDate = vacationDateStr;
                                nextHolidayItem = holidayItem;
                            }
                        }
                    }
                }
            }
            if (nextHolidayItem != null) {
                return new NextHolidayInfo(String.valueOf(minDays), nextHolidayItem.getString("name"), nextHolidayDate, nextHolidayItem.getString("tip"), nextHolidayItem.getString("rest"));
            }
        } catch (Exception e) {
            log.error("获取下一个节假日信息失败", e);
        }
        return new NextHolidayInfo("", "", "", "", "");
    }
}
