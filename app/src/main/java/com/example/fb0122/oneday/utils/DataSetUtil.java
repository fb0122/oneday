package com.example.fb0122.oneday.utils;

import android.content.ContentValues;
import android.content.Context;

import db_oneday.OneDaydb;

/**
 * Created by fengbo on 16/8/19.
 */
public class DataSetUtil {

    public static ContentValues updateData(String columnName,String value){
        ContentValues cv = new ContentValues();
        cv.put(columnName,value);
        return cv;
    }

}
