package com.example.fb0122.oneday;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<Fragment> list = new ArrayList<Fragment>();
    public MyViewPager viewpager;
    private FragmentAdapter fg;
    private static int selectedpage = 0;
    private boolean scrollble = true;
    public static String title = "oneday";
    public static int screenWidth;

    public ImageView tv1;
    public ImageView tv2;
    public ImageView tv3;
    private AtyDay day;
    private HashMap<Integer, Object> listdata = new HashMap<>();
    private LinearLayout dayLayout, weekLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        listdata = day.map;
        day = new AtyDay(this, listdata);
        AtyFinish finish = new AtyFinish();
        list.add(day);
//		list.add(month);
        list.add(finish);
        viewpager = (MyViewPager) findViewById(R.id.viewpager);
        tv1 = (ImageView) findViewById(R.id.tv1);
        tv2 = (ImageView) findViewById(R.id.tv2);
        tv3 = (ImageView) findViewById(R.id.tv3);
        dayLayout = (LinearLayout) findViewById(R.id.layoutDay);
        weekLayout = (LinearLayout) findViewById(R.id.layoutWeek);

        dayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedpage == 1) {
                    viewpager.setCurrentItem(0, true);
//				overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                    tv1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.GONE);
                }
            }
        });

        weekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedpage == 0) {
                    viewpager.setCurrentItem(1, true);
                    tv1.setVisibility(View.GONE);
                    tv2.setVisibility(View.VISIBLE);
                }
            }
        });

        fg = new FragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(fg);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                selectedpage = arg0;
                switch (selectedpage) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                setScrollble(false);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

                setScrollble(true);

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!scrollble) {
            return false;
        }
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }


    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

    public class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

    }

    public void pageBack(View v) {
        if (v.getId() == R.id.layoutDay) {
            if (selectedpage == 1) {
                viewpager.setCurrentItem(0, true);
//				overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.GONE);
            }
        }
    }


    public void pageSwitch(View v) {
        if (v.getId() == R.id.layoutWeek) {
            if (selectedpage == 0) {
                viewpager.setCurrentItem(1, true);
                tv1.setVisibility(View.GONE);
                tv2.setVisibility(View.VISIBLE);
            }
        }
    }
}
