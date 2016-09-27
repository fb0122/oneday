package com.example.fb0122.oneday.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by fengbo on 16/8/18.
 *
 * 用来获取时间的工具类.
 *
 */
public class TimeCalendar {

    public static String[] week_str = new String[]{"周一","周二","周三","周四","周五","周六","周日"};
    private static HashMap<String,Integer> weekMap = new HashMap<>();
    private static HashMap<Integer,String> weekMap1 = new HashMap<>();

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


    public static String getSpecialWeek(int day){
        return week_str[day % 7];
    }

    public static String getTodayDate(){
        String date = dealStr(String.valueOf(getCanlendar().get(Calendar.MONTH) + 1)) + " . " + dealStr(String.valueOf(getCanlendar().get(Calendar.DAY_OF_MONTH)));
        return date;
    }

    public static int getWeekInYear(){
        int week  = getCanlendar().get(Calendar.WEEK_OF_YEAR);
        return week;
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

    public static HashMap<String,Integer> getWeekMap(){
        weekMap.put("周一",1);
        weekMap.put("周二",2);
        weekMap.put("周三",3);
        weekMap.put("周四",4);
        weekMap.put("周五",5);
        weekMap.put("周六",6);
        weekMap.put("周日",7);
        return weekMap;
    }

    public static HashMap<Integer, String> getWeekInMap(){
        weekMap1.put(1,"周一");
        weekMap1.put(2,"周二");
        weekMap1.put(3,"周三");
        weekMap1.put(4,"周四");
        weekMap1.put(5,"周五");
        weekMap1.put(6,"周六");
        weekMap1.put(7,"周日");
        return weekMap1;
    }

}
