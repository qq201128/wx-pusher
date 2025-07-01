package com.wang.strategy.template;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.common.WxConstants;
import com.wang.common.WxTemplateConstants;
import com.wang.domain.DistrictInfo;
import com.wang.domain.IdentityInfo;
import com.wang.exception.WxException;
import com.wang.mapper.DistrictInfoMapper;
import com.wang.strategy.WxTemplateStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

/**
 * 普通早安消息推送策略
 */
@Service(WxTemplateConstants.COMMON_MORNING)
@Slf4j
@RequiredArgsConstructor
public class CommonMorningStrategy implements WxTemplateStrategy {
    private final DistrictInfoMapper districtInfoMapper;

    @Override
    public void execute(WxMpTemplateMessage wxMpTemplateMessage, IdentityInfo identityInfo) {

    }

    private Integer getDistrictCode(IdentityInfo identityInfo) {
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
