package com.example.fb0122.oneday;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

    public boolean isScroll;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyViewPager(Context context) {
        super(context);
    }

    public void setScanScroll(boolean isScroll) {
        this.isScroll = isScroll;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(isScroll){
            return super.onInterceptTouchEvent(arg0);
        }else{
            return false;
        }
    }

}
