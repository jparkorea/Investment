package com.kakaopay.codingTest.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * 1초 = 1000
     */
    public static final int MSEC = 1000;
    /**
     * 1분 = 1000 * 60 으로 정의
     */
    public static final int MIN_MSEC = DateUtils.MSEC * 60;

    /**
     * 1시간 = 1000 * 60 * 60
     */
    public static final int HOUR_MSEC = DateUtils.MIN_MSEC * 60;

    /**
     * 1일 = 1000 * 60 * 60 * 24
     */
    public static final int DAY_MSEC = DateUtils.HOUR_MSEC * 24;

    // 일 수 추가
    public static Date addDays(Date current, Integer addDays){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, addDays);
        return new Date(calendar.getTimeInMillis());
    }
}
