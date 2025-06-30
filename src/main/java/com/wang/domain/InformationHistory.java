package com.wang.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("information_history")
public class InformationHistory {
    private String openId;
    private String information;
    private Date createDate;
}
