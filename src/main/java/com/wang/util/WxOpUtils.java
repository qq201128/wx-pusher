package com.wang.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wang.common.WxConstants;
import com.wang.domain.IdentityInfo;
import com.wang.domain.ResponseMessage;
import com.wang.exception.WxException;
import com.wang.observer.WxSubscriber;
import com.wang.mapper.DistrictInfoMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 操作工具类
 */
@Slf4j
public class WxOpUtils {
    private WxOpUtils() {
    }
    @Resource
    private static DistrictInfoMapper districtInfoMapper;

    public static void returnMessages(HttpServletResponse response, String msgType, String openId, String officialAccount,
                                      String returnContent) throws IOException {
        try (PrintWriter printWriter = response.getWriter()) {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setFromUserName(officialAccount);
            responseMessage.setToUserName(openId);
            responseMessage.setMsgType(msgType);
            responseMessage.setCreateTime(System.currentTimeMillis());
            responseMessage.setContent(returnContent);
            // 转化成微信可接收的xml信息返回
            String transformedMessage = transformMessage(responseMessage);
            log.info(">>> reply message : {}", responseMessage.getContent());
            printWriter.println(transformedMessage);
        }
    }

    /**
     * 将信息转换成xml返回
     *
     * @param textMessage 存储回复信息的对象
     * @return 微信指定接受的回复信息格式（xml）
     */
    private static String transformMessage(ResponseMessage textMessage) {
        return "<xml>\n" +
                "  <ToUserName><![CDATA[" + textMessage.getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + textMessage.getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + textMessage.getContent() + "]]></Content>\n" +
                "</xml>";
    }

    /**
     * 获取访问令牌
     *
     * @return {@link String}
     */
    public static String getAccessToken() {
        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&" +
                "appid=" + WxConstants.APP_ID + "&secret=" + WxConstants.APP_SECRET;
        String accessTokenStr = HttpUtil.get(accessTokenUrl);
        JSONObject jsonObject = JSONObject.parseObject(accessTokenStr);
        Object accessTokenObj = jsonObject.get("access_token");
        if (accessTokenObj == null) {
            String errMsg = "获取access_token失败，返回内容：" + accessTokenStr;
            throw new WxException(errMsg);
        }
        return accessTokenObj.toString();
    }

    /**
     * 得到wx订阅者
     *
     * @param requestMap 请求映射
     * @return {@link WxSubscriber}
     */
    public static WxSubscriber getWxSubscriber(Map<String, String> requestMap) {
        // 发送方账号
        String openId = requestMap.get("FromUserName");
        // 公众号
        String officialAccount = requestMap.get("ToUserName");
        IdentityInfo identityInfo = new IdentityInfo();
        identityInfo.setAppId(WxConstants.APP_ID);
        identityInfo.setAppSecret(WxConstants.APP_SECRET);
        identityInfo.setOpenId(openId);
        identityInfo.setPublicId(officialAccount);
        return new WxSubscriber(identityInfo);
    }

    /**
     * 复制属性不是空的字段
     *
     * @param source 源
     * @param target 目标
     */
    public static void copyPropertiesNotNull(Object source, Object target) {
        // 获取非空字段拷贝，第三个参数获取的空字段用来排除
        BeanUtils.copyProperties(source, target, getIgnoreProperties(source));
    }

    /**
     * 得到空字段属性
     *
     * @param source 源
     * @return {@link String[]}
     */
    private static String[] getIgnoreProperties(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> properties = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                Object value = beanWrapper.getPropertyValue(name);
                if (value == null) {
                    properties.add(name);
                }
            }
        }
        return properties.toArray(new String[0]);
    }


    public static Long countDays(String beginDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date begin = sdf.parse(beginDate);
            Date end = sdf.parse(endDate);
            return DateUtil.between(begin, end, DateUnit.DAY);
        } catch (ParseException e) {
            log.error("日期解析失败：{}", e.getMessage());
        }
        return -1L;
    }
}
