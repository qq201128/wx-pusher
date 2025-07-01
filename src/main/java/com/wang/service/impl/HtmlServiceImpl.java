package com.wang.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wang.domain.InformationHistory;
import com.wang.mapper.InformationHistoryMapper;
import com.wang.service.HtmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HtmlServiceImpl implements HtmlService {
    private final InformationHistoryMapper informationHistoryMapper;
    @Override
    public InformationHistory getHtml(String openId) {
        // 按 openId 查询该用户最新记录
        return informationHistoryMapper.selectOne(Wrappers.<InformationHistory>query().lambda()
                .eq(InformationHistory::getOpenId, openId)
                .orderByDesc(InformationHistory::getCreateDate)
                .last("limit 1"));
    }
}
