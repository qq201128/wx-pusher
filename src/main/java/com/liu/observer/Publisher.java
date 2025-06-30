package com.liu.observer;

import com.liu.enums.WxTemplateType;

/**
 * 发布者
 */
public interface Publisher {
    /**
     * 附加
     *
     * @param subscriber 订阅者
     */
    void attach(Subscriber subscriber);

    /**
     * 分离
     *
     * @param subscriber 订阅者
     */
    void detach(Subscriber subscriber);

    /**
     * 通知
     *
     * @param wxTemplateType wx模板类型
     */
    void inform(WxTemplateType wxTemplateType);
}
