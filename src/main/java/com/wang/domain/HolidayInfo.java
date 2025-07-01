package com.wang.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
public class HolidayInfo {
    private String holidayInfo;
    private Integer daycode;
    private String name;
    private String info;
    public HolidayInfo(String holidayInfo, int daycode, String name, String info) {
        this.holidayInfo = holidayInfo;
        this.daycode = daycode;
        this.name = name;
        this.info = info;
    }
}
