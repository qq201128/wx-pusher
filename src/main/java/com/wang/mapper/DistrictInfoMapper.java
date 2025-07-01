package com.wang.mapper;

import com.wang.domain.DistrictInfo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 *  区域信息存储
 */
@Repository
public interface DistrictInfoMapper extends BaseMapperPlus<DistrictInfo,DistrictInfo> {
}
