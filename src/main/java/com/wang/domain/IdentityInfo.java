package com.wang.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 *  身份信息
 */

@Data
@TableName("identity_info")
public class IdentityInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 公众号appId
     */
    private String appId;
    private String appSecret;

    private String openId;

    /**
     * 公众号
     */

    private String publicId;

    /**
     * 纬度
     */

    private String latitude;

    /**
     * 经度
     */

    private String longitude;

    /**
     * 准确度
     */

    private String precisionInfo;

    /**
     * 国家
     */

    private String country;

    /**
     * 省
     */

    private String province;

    /**
     * 市
     */

    private String city;

    /**
     * 区
     */

    private String district;

    /**
     * 详细地址
     */

    private String address;

    /**
     * 0表示正常关注，1表示取消关注
     */

    private Integer status = 0;


}
