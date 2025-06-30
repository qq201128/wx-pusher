package com.wang.repository;

import com.wang.dto.DistrictInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *  区域信息存储
 */
public interface DistrictInfoRepository extends JpaRepository<DistrictInfo, Integer> {
    /**
     * 地区代码
     *
     * @param city     城市
     * @param district 区
     * @return {@link Integer}
     */
    @Query("SELECT d.districtCode FROM DistrictInfo d where d.city like ?1% and d.district like ?2%")
    Integer getDistrictCode(String city, String district);
}
