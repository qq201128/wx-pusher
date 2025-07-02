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
import com.wang.mapper.SongMapper;
import com.wang.mapper.TrSongMapper;
import com.wang.strategy.WxTemplateStrategy;
import com.wang.util.WxOpUtils;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  特殊早安推送策略
 */
@Service(WxTemplateConstants.SPECIAL_MORNING)
@RequiredArgsConstructor
public class SpecialMorningStrategy implements WxTemplateStrategy {

    private final SiliconFlowAIServiceImpl siliconFlowAIService;

    private final DistrictInfoMapper districtInfoMapper;
    private final InformationHistoryMapper informationHistoryMapper;
    private static final String SONG_URL = "https://api.bugpk.com/api/163_music?ids=";
    private final SongMapper songMapper;
    private final TrSongMapper trSongMapper;
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

        StringBuilder stringBuilder = new StringBuilder();
        Song song = getSong(identityInfo.getOpenId());
        if (song!=null){
            stringBuilder.append("歌曲名称为：").append(song.getSongName()).append("歌手为：").append(song.getSinger()).append("歌曲链接为：").append(song.getSongUrl()).append("歌曲图片为：").append(song.getSongPicture());
        }
        stringBuilder.append("我们的恋爱时间为：").append(WxConstants.LOVE_DATE);

        String htmlContent = siliconFlowAIService.chatToSpecialMorning(wxMpTemplateMessage.getData().toString() + stringBuilder.toString());

        wxMpTemplateMessage.setUrl("https://3751016qc9ar.vicp.fun?openid="+identityInfo.getOpenId());

        InformationHistory informationHistory = new InformationHistory();
        informationHistory.setOpenId(identityInfo.getOpenId());
        informationHistory.setInformation(htmlContent);
        informationHistory.setCreateDate(new Date());
        informationHistoryMapper.insert(informationHistory);


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

    private Song getSong(String openId){
        List<Song> songs;
        //查询是否有专属的歌单
        List<TrSong> trSongs = trSongMapper.selectList(Wrappers.<TrSong>query().lambda()
                .eq(TrSong::getOpenId, openId));
        if (trSongs !=null && !trSongs.isEmpty()){
            Set<Long> songIds = trSongs.stream().collect(Collectors.groupingBy(TrSong::getSongId)).keySet();
            songs = songMapper.selectList(Wrappers.<Song>query().lambda()
                    .in(Song::getId,songIds));
        }else {
            songs = songMapper.selectList();
        }

        if (songs == null || songs.isEmpty()) {
            return null;
        }
        int randomIndex = java.util.concurrent.ThreadLocalRandom.current().nextInt(songs.size());
        Song song = songs.get(randomIndex);

        String respStr = HttpUtil.get(SONG_URL + song.getSongId() + "&level=hires&type=json");
        JSONObject respJson = JSONObject.parseObject(respStr);
        // 直接提取url字段
        String url = respJson.getString("url");
        String picture = respJson.getString("pic");
        song.setSongUrl(url);
        song.setSongPicture(picture);

        return song;
    }

}
