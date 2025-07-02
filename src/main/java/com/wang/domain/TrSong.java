package com.wang.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tr_song")
public class TrSong {
    private Long songId;
    private String openId;
}
