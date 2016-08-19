package com.example.fb0122.oneday;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.fb0122.oneday.utils.TimeCalendar;

import java.util.Calendar;

/**
 * Created by fengbo on 16/8/17.
 */
public class SplashActivity extends AppCompatActivity {

    private final static String TAG = "SplashActivity";

    TextView weekTextView;
    TextView tipsTextView;
    private static View view;
    String week;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(getBaseContext(),R.layout.activity_splash,null);
        setContentView(view);
        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpTo();
            }
        },0);

    }

    private void initView(){
        weekTextView = (TextView)findViewById(R.id.text_splash_week);
        tipsTextView = (TextView)findViewById(R.id.text_splash_tips);
    }


    private void jumpTo(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1f);
        alphaAnimation.setDuration(1000);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Calendar calendar = Calendar.getInstance();
                week = TimeCalendar.getTodayWeek();
                weekTextView.setText("今天是" + week);
                tipsTextView.setText("好心情每一天");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                intent.putExtra("week",week);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
}
