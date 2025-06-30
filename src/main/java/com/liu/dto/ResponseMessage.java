package com.liu.dto;

import lombok.Data;

/**
 * 返回信息
 */
@Data
public class ResponseMessage {
    /**
     * 开发者微信号
     */
    private String toUserName;
    /**
     * 发送方账号
     */
    private String fromUserName;
    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息文本
     */
    private String content;
}
