package com.wang.domain;

import lombok.Data;

@Data
public class NextHolidayInfo {
    private String daysToNextHoliday;
    private String nextHolidayName;
    private String nextHolidayDate;
    private String nextHolidayTip;
    private String nextHolidayRest;
    public NextHolidayInfo(String daysToNextHoliday, String nextHolidayName, String nextHolidayDate, String nextHolidayTip, String nextHolidayRest) {
        this.daysToNextHoliday = daysToNextHoliday;
        this.nextHolidayName = nextHolidayName;
        this.nextHolidayDate = nextHolidayDate;
        this.nextHolidayTip = nextHolidayTip;
        this.nextHolidayRest = nextHolidayRest;
    }
}
