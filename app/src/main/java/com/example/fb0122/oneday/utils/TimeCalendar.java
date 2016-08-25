package com.example.fb0122.oneday.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by fengbo on 16/8/18.
 *
 * 用来获取时间的工具类.
 *
 */
public class TimeCalendar {

    public static String[] week_str = new String[]{"周一","周二","周三","周四","周五","周六","周日"};


    public static Calendar getCanlendar(){
        return Calendar.getInstance();
    }

    public TimeCalendar() {
    }

    public static int getToday(){
        return getCanlendar().get(Calendar.DAY_OF_WEEK);
    }

    public static String getTodayWeek(){
        return week_str[getToday() - 2];
    }

    public static int getDateToday(){
        return getCanlendar().get(Calendar.DAY_OF_YEAR);
    }

}
