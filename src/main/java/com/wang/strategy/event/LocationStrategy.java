package com.wang.strategy.event;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.common.WxConstants;
import com.wang.domain.IdentityInfo;
import com.wang.observer.WxPublisher;
import com.wang.observer.WxSubscriber;
import com.wang.mapper.IdentityInfoMapper;
import com.wang.strategy.WxEventStrategy;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Map;

/**
 *  获取位置的策略
 */
@Service("LOCATION")
@Slf4j
public class LocationStrategy implements WxEventStrategy {
    @Resource
    private WxPublisher wxPublisher;

    @Resource
    private IdentityInfoMapper identityInfoMapper;

    @Override
    public void execute(Map<String, String> requestMap, HttpServletResponse response) throws Exception {
        log.info(">>> 事件类型：LOCATION");
        IdentityInfo identityInfo = new IdentityInfo();
        identityInfo.setAppId(WxConstants.APP_ID);
        identityInfo.setAppSecret(WxConstants.APP_SECRET);
        String openId = requestMap.get("FromUserName");
        identityInfo.setOpenId(openId);
        identityInfo.setPublicId(requestMap.get("ToUserName"));
        // 纬度
        String latitude = requestMap.get("Latitude");
        identityInfo.setLatitude(latitude);
        // 经度
        String longitude = requestMap.get("Longitude");
        identityInfo.setLongitude(longitude);
        identityInfo.setPrecisionInfo(requestMap.get("Precision"));
        // 根据经纬度获取地址的url
        String searchAddressUrl = "https://api.map.baidu.com/reverse_geocoding/v3/?ak=" + WxConstants.BAI_DU_AK +
                "&output=json&coordtype=wgs84ll&location=" + latitude + "," + longitude;
        String addressStr = HttpUtil.get(searchAddressUrl);
        // 处理json串的jsonObject对象
        String result = JSONObject.parseObject(addressStr).get("result").toString();
        JSONObject resultObject = JSONObject.parseObject(result);
        // 详细地址
        String address = resultObject.get("formatted_address").toString();
        JSONObject addressComponent = JSONObject.parseObject(resultObject.get("addressComponent").toString());
        // 国家
        String country = addressComponent.get("country").toString();
        // 省份
        String province = addressComponent.get("province").toString();
        // 市
        String city = addressComponent.get("city").toString();
        // 区
        String district = addressComponent.get("district").toString();
        identityInfo.setAddress(address);
        identityInfo.setCountry(country);
        identityInfo.setProvince(province);
        identityInfo.setCity(city);
        identityInfo.setDistrict(district);
        log.info(">>> 更新用户位置信息：{}", identityInfo);
        // 更新数据库里的位置信息(走到位置策略这里一定是在表中有记录，直接更新全量的数据)
        identityInfoMapper.update(identityInfo, Wrappers.<IdentityInfo>query().lambda()
                .eq(IdentityInfo::getAppId, identityInfo.getAppId())
                .eq(IdentityInfo::getAppSecret, identityInfo.getAppSecret())
                .eq(IdentityInfo::getOpenId, identityInfo.getOpenId()));
        WxSubscriber wxSubscriber = new WxSubscriber(identityInfo);
        // 更新位置信息
        wxPublisher.update(wxSubscriber);
    }
}
