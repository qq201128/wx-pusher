package com.wang.mapper;

import com.wang.domain.DistrictInfo;
import org.apache.ibatis.annotations.Select;

/**
 *  区域信息存储
 */
public interface DistrictInfoMapper extends BaseMapperPlus<DistrictInfo,DistrictInfo> {
    /**
     * 地区代码
     *
     * @param city     城市
     * @param district 区
     * @return {@link Integer}
     */
    @Select("SELECT d.districtCode FROM weather_district_id d where d.city like ?1% and d.district like ?2%")
    Integer getDistrictCode(String city, String district);
}
