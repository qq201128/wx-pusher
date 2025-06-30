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
    public InformationHistory getHtml() {
        //todo 因目前无法确定到底是那个用户 所以使用最新的记录
        return informationHistoryMapper.selectOne(Wrappers.<InformationHistory>query().lambda()
                .last("limit 1")
                .orderByDesc(InformationHistory::getCreateDate));
    }
}
