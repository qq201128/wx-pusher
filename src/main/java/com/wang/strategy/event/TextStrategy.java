package com.wang.strategy.event;

import com.wang.enums.WxMessageType;
import com.wang.strategy.WxEventStrategy;
import com.wang.util.WxOpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * 消息策略
 */
@Service("text")
@Slf4j
public class TextStrategy implements WxEventStrategy {
    @Override
    public void execute(Map<String, String> requestMap, HttpServletResponse response) throws Exception {
        // 发送方账号
        String openId = requestMap.get("FromUserName");
        // 公众号
        String officialAccount = requestMap.get("ToUserName");
        String acceptContent = requestMap.get("Content");
        log.info(">>> 用户输入：{}", acceptContent);
        // 用户输入信息后返回信息
        String returnContent = "I don't know what to return at the moment. I'll come back when I think of it~";
        WxOpUtils.returnMessages(response, WxMessageType.TEXT.getType(), openId, officialAccount, returnContent);
    }
}
