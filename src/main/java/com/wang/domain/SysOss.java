package com.wang.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("oss")
public class SysOss {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 文件名
     */
    private String fileName;

    /**
     * URL地址
     */
    private String url;
}
