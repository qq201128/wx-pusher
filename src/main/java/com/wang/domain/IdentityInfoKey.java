package com.wang.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;


import java.io.Serializable;

/**
 * 身份信息的主键
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityInfoKey implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 公众号appId
     */
    private String appId;


    private String appSecret;


    private String openId;


}
