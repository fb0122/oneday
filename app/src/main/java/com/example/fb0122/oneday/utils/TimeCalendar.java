package com.example.fb0122.oneday.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Log.d("Time","day " + getCanlendar().get(Calendar.DAY_OF_WEEK));
        return getCanlendar().get(Calendar.DAY_OF_WEEK);
    }

    public static String getTodayWeek(){
        return week_str[getToday() - 2];
    }

    public static String getSpecialWeek(int day){
        return week_str[getToday() - day];
    }

    public static String getTodayDate(){
        String date = dealStr(String.valueOf(getCanlendar().get(Calendar.MONTH) + 1)) + " . " + dealStr(String.valueOf(getCanlendar().get(Calendar.DAY_OF_MONTH)));
        return date;
    }

    private static String dealStr(String str){
        if (str.length() == 1){
            str = "0" + str;
        }
        return str;
    }

    //获取 今天之后一周的日期,通过推后 day 天来计算.
    public static String getLaterDate(int day){
        Date date = new Date();
        Calendar calendar = getCanlendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,day);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date_str = formatter.format(date);
        Log.d("Time",date_str.substring(4,date_str.length()));
        return date_str.substring(5,date_str.length());
    }


}
