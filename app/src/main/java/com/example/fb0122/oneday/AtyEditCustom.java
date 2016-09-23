package com.example.fb0122.oneday;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fb0122.oneday.utils.TimeCalendar;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import db_oneday.OneDaydb;
import db_oneday.ScheduleData;
import oneday.Alarm.Config;

public class AtyEditCustom extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, OnClickListener {

    private final static String TAG = "AtyEditCustom";

    Toolbar toolbar;

    protected static final String TIMEPICKER_TAG = "timepickerdialog";
    private TextView etCusTime, etCusToTime;
    final Calendar calendar = Calendar.getInstance();
    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
    boolean c;
    private EditText etCustom;
    private SQLiteDatabase dbwrite;
    private TextView Sun, Mon, Tue, Wed, Thu, Fri, Sat;
    ContentValues cv = new ContentValues();
    PendingIntent pending;
    public static int a = 0;
    public static int clickId;
    public static String title = "oneday";
    public static List<Integer> list = new ArrayList<>();

    private OneDaydb db = new OneDaydb(this, "oneday");
    private Calendar alacale = Calendar.getInstance();
    private AlarmManager alarm;
    private String spinnerTick;
    private ArrayList<String> saveWeek = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        etCusTime = (TextView) findViewById(R.id.etCusTime);
        etCusToTime = (TextView) findViewById(R.id.etCusToTime);
        etCustom = (EditText) findViewById(R.id.etCustom);

        //清楚list内存储的选择星期的数据
        list.clear();

        initView();

        //Material Desigh Toolbar loading
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Spinner s = (Spinner) findViewById(R.id.etExRemind);
        ArrayAdapter spadapter = ArrayAdapter.createFromResource(this, R.array.repece_times, android.R.layout.simple_dropdown_item_1line);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //获取spinner选择字符
        spinnerTick = s.getSelectedItem().toString();


        s.setAdapter(spadapter);

        dbwrite = db.getWritableDatabase();

        etCusTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                c = true;
                timepicker();
            }
        });
        etCusToTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                c = false;
                timepicker();

            }
        });

    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Sun = (TextView) findViewById(R.id.Sun);
        Mon = (TextView) findViewById(R.id.Mon);
        Tue = (TextView) findViewById(R.id.Tue);
        Wed = (TextView) findViewById(R.id.Wed);
        Thu = (TextView) findViewById(R.id.Thu);
        Fri = (TextView) findViewById(R.id.Fri);
        Sat = (TextView) findViewById(R.id.Sat);

        Sun.setOnClickListener(this);
        Mon.setOnClickListener(this);
        Tue.setOnClickListener(this);
        Wed.setOnClickListener(this);
        Thu.setOnClickListener(this);
        Fri.setOnClickListener(this);
        Sat.setOnClickListener(this);

    }


    public void Done(View v) {
        if (saveWeek.size() > 0) {
            for (int j = 0; j < saveWeek.size(); j++) {
                Log.e(TAG,"insert");
                cv.put(OneDaydb.COLUMN_PLAN, etCustom.getText().toString());
                cv.put(OneDaydb.COLUMN_FROM_TIME, etCusTime.getText().toString());
                cv.put(OneDaydb.COLUMN_TO_TIME, etCusToTime.getText().toString());
                cv.put(OneDaydb.COLUMN_WEEK,"周" + saveWeek.get(j));
                dbwrite.insert(OneDaydb.TABLE_NAME, null, cv);
            }
        }else {
            Calendar cl = Calendar.getInstance();
            cv.put(OneDaydb.COLUMN_PLAN, etCustom.getText().toString());
            cv.put(OneDaydb.COLUMN_FROM_TIME, etCusTime.getText().toString());
            cv.put(OneDaydb.COLUMN_TO_TIME, etCusToTime.getText().toString());
            cv.put(OneDaydb.COLUMN_WEEK, TimeCalendar.getTodayWeek());
            dbwrite.insert(OneDaydb.TABLE_NAME, null, cv);
        }
            setResult(Config.CHANGE_DATA);
            finish();
    }

    public void timepicker() {
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
    }


    public void backTo(View v) {

        finish();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (c) {
            if (minute < 10) {
                String minute_str = "0" + minute;
                etCusTime.setText(hourOfDay + ":" + minute_str);
            } else {
                etCusTime.setText(hourOfDay + ":" + minute + "");
            }
            alacale.setTimeInMillis(System.currentTimeMillis());
            alacale.set(Calendar.HOUR_OF_DAY, hourOfDay);
            alacale.set(Calendar.MINUTE, minute);
            alacale.set(Calendar.SECOND, 0);
            alacale.set(Calendar.MILLISECOND, 0);

            doAlarm(alacale);

        } else {
            if (minute < 10) {
                String minute_str = "0" + minute;
                etCusToTime.setText(hourOfDay + ":" + minute_str);
            } else {
                etCusToTime.setText(hourOfDay + ":" + minute + "");
            }
        }
    }

    public void doAlarm(Calendar alacal) {

        Intent ii = new Intent(AtyEditCustom.this, ReceiverNo.class);

        pending = PendingIntent.getBroadcast(AtyEditCustom.this, 0, ii, 0);

        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, alacale.getTimeInMillis(), 10 * 1000, pending);

    }

    /*
        根据设置的提前时间计算需要提示的时间
     */
    private String dealTime(String spinner,String originTime){
        int hour  = Integer.valueOf(originTime.split(":")[0]);
        int minute = Integer.valueOf(originTime.split(":")[1]);
        switch (spinner){
            case "提前10分钟":
                minute -= 10;
            break;
            case "提前30分钟":
                minute -= 10;
                break;
            case "提前1小时":
                break;
            case "提前2小时":
                break;

        }
        return originTime;
    }

    /*
        计算时间的函数
     */
    private int calculatorTime(){
        return 0;
    }

    public static String translateWeek(int week){
        String week_str = null;
        switch (week){
            case 1:
                week_str = "一";
                break;
            case 2:
                week_str = "二";
                break;
            case 3:
                week_str = "三";
                break;
            case 4:
                week_str = "四";
                break;
            case 5:
                week_str = "五";
                break;
            case 6:
                week_str = "六";
                break;
            case 7:
                week_str = "日";
                break;
        }
        return "周" + week_str;
    }

    @Override
    public void onClick(View view) {
        TextView tv = (TextView) view;
        if (tv.getTag()!= null && (int)tv.getTag() == 0) {
            tv.setBackground(getResources().getDrawable(R.drawable.week_click));
            saveWeek.remove(tv.getText().toString());
            tv.setTag(1);
        } else {
            tv.setBackground(getResources().getDrawable(R.drawable.double_click));
            saveWeek.add(tv.getText().toString());
            tv.setTag(0);
        }

    }

}
