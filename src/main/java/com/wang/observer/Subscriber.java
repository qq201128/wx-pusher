package com.wang.observer;

import com.wang.dto.IdentityInfo;
import com.wang.enums.WxTemplateType;

/**
 * 订阅者
 */
public interface Subscriber {
    /**
     * 更新
     * 根据模板id接受信息显示
     *
     * @param wxTemplateType wx模板类型
     */
    void update(WxTemplateType wxTemplateType);


    /**
     * 得到身份信息
     *
     * @return {@link IdentityInfo}
     */
    IdentityInfo getIdentityInfo();
}
