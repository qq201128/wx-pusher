package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.errorprone.annotations.Var;
import com.wang.chat.service.impl.SiliconFlowAIServiceImpl;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.domain.*;
import com.wang.exception.WxException;
import com.wang.mapper.*;
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
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    private static final String SONG_URL = "https://api.bugpk.com/api/163_music?ids=";
    //随机热歌
    private static final String HOT_SONG_URL = "https://api.zxki.cn/api/wyyrg";
    private final SysOssMapper ossMapper;

    private final DistrictInfoMapper districtInfoMapper;
    private final SiliconFlowAIServiceImpl siliconFlowAIService;
    private final InformationHistoryMapper informationHistoryMapper;
    private final SongMapper songMapper;
    private final TrSongMapper trSongMapper;

    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        String today = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String work = getWorkQuote();
        HolidayInfo holidayInfo = getHolidayInfo(today);
        NextHolidayInfo nextHolidayInfo = getNextHolidayInfo(today);
        Song song = getSong(identityInfo.getOpenId());
        if (song!=null){
            stringBuilder.append("我要听的歌曲名称为：").append(song.getSongName()).append("歌手为：").append(song.getSinger()).append("歌曲链接为：").append(song.getSongUrl()).append("歌曲图片为：").append(song.getSongPicture());
        }
        List<String> photoList = getPhoto();
        if (photoList!=null){
            for (String photo : photoList){
                stringBuilder.append("我的照片链接为：").append(photo);
            }

        }
        Long meetDays = WxOpUtils.countDays(WxConstants.LOVE_DATE, today);
        wxMpTemplateMessage.addData(new WxMpTemplateData("love_days", String.valueOf(meetDays)));
        wxMpTemplateMessage.addData(new WxMpTemplateData("work", work));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday", holidayInfo.getHolidayInfo()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("days_to_next_holiday", nextHolidayInfo.getDaysToNextHoliday()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("next_holiday_name", nextHolidayInfo.getNextHolidayName()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("next_holiday_date", nextHolidayInfo.getNextHolidayDate()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday_tip", nextHolidayInfo.getNextHolidayTip()));
        wxMpTemplateMessage.addData(new WxMpTemplateData("holiday_rest", nextHolidayInfo.getNextHolidayRest()));
        stringBuilder.append("我们的恋爱时间为：").append(WxConstants.LOVE_DATE);
        String htmlContent = siliconFlowAIService.chatToCommonMorning(wxMpTemplateMessage.getData().toString() + stringBuilder.toString());

        wxMpTemplateMessage.setUrl("https://3751016qc9ar.vicp.fun?openid=" + identityInfo.getOpenId());
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

    private Song getSong(String openId){
        String respStr = HttpUtil.get(HOT_SONG_URL);
        JSONObject respJson = JSONObject.parseObject(respStr);
        JSONObject data = respJson.getJSONObject("data");
        if (data == null) {
            return null;
        }
        String name = data.getString("name");
        String url = data.getString("url");
        String picUrl = data.getString("picurl");
        String artistsName = data.getString("artistsname");
        String id = data.getString("id");

        Song song = new Song();
        song.setSongName(name);
        song.setSongUrl(url);
        song.setSongId(Long.valueOf(id));
        song.setSongPicture(picUrl);
        song.setSinger(artistsName);
        songMapper.insert(song);

        return song;
    }
    private List<String> getPhoto() {
        List<SysOss> sysOssList = ossMapper.selectList();
        List<String> photoUrls = new ArrayList<>();
        int total = sysOssList.size();
        if (total == 0) {
            return photoUrls;
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            indices.add(i);
        }
        // 打乱索引顺序
        java.util.Collections.shuffle(indices);
        int count = Math.min(4, total);
        for (int i = 0; i < count; i++) {
            photoUrls.add(sysOssList.get(indices.get(i)).getUrl());
        }
        return photoUrls;
    }
}
