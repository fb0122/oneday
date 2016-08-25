package com.example.fb0122.oneday.utils;

import android.content.Context;

/**
 * Created by fengbo on 16/8/25.
 */
public class DimenTranslate {

    public static float px2dp(Context context, int px) {
        return (float) px / context.getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }

    public static float dp2sp(Context context, int dip) {
        return px2dp(context, dp2px(context, dip));
    }

    public static int dp2px(Context context, int dpValue){
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5);
    }

    public static int dp2px(Context context, float dpValue){
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5);
    }

}
