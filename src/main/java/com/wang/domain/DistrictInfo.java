package com.wang.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 全国各个地区的districtCode、经纬度信息
 */
@TableName("weather_district_id")
@Data
public class DistrictInfo {

    private Integer areaCode;


    private Integer districtCode;


    private Integer cityGeocode;


    private String city;


    private Integer districtGeocode;


    private String district;



    private String lon;


    private String lat;


    private String staFc;


    private String staRt;


    private String province;


    private String fcLon;


    private String fcLat;


    private String rtLon;


    private String rtLat;


    private Integer originAreacode;


    private Integer excludeInfo;


}
