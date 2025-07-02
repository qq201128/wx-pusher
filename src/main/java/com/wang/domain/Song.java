package com.wang.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("song")
public class Song {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 歌曲id
     */
    private Long songId;
    /**
     * 歌曲名称
     */
    private String songName;
    /**
     * 歌手
     */
    private String singer;
    /**
     * 歌曲url
     */
    private String songUrl;
    /**
     * 歌曲图片
     */
    private String songPicture;

}
