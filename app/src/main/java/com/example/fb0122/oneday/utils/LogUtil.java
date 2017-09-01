package com.example.fb0122.oneday.utils;

import android.util.Log;

/**
 * Created by fb on 2017/9/1.
 */

public class LogUtil {

  private static final String tag_head = "fb:-->oneday:";

  public static void d(String tag, String content){
    Log.e(tag_head + tag, content);
  }

}
