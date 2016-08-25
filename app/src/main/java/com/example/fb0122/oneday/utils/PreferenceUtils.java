package com.example.fb0122.oneday.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by fb0122 on 2016/8/25.
 */
public class PreferenceUtils {

    private final static String DONE = "done";

    public static void putBoolean(Context context, String key,boolean done){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key,done).commit();
    }

    public static boolean getBoolean(String key,Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key,false);
    }

}
