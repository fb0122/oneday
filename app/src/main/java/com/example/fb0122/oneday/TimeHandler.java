package com.example.fb0122.oneday;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.fb0122.oneday.utils.TimeCalendar;

import java.util.ArrayList;

import db_oneday.OneDaydb;
import oneday.Alarm.Config;
import oneday.Alarm.NotifyService;


public class TimeHandler extends Handler {

    Cursor c;
    private SQLiteDatabase dbreader;
    private String time;
    private ArrayList<String> notifyList = new ArrayList<>();
    private String today;
    private Context mContext;
    private OneDaydb db;

    public TimeHandler(Looper looper,Context context) {
        super(looper);
        today = TimeCalendar.getTodayWeek();
        mContext = context;
        db = new OneDaydb(context, OneDaydb.TABLE_NAME);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Config.ADD_NOTIFY:
                dbreader = db.getReadableDatabase();
                c = dbreader.rawQuery("select " + OneDaydb.COLUMN_FROM_TIME + " from " + OneDaydb.TABLE_NAME
                        + " where " + OneDaydb.COLUMN_WEEK + "=" + "'" + today + "'",null);
                if (c.moveToFirst()) {
                    notifyList.clear();
                    do {
                        time = c.getString(c.getColumnIndex(OneDaydb.COLUMN_FROM_TIME));
                        notifyList.add(time);
                    } while (c.moveToNext());
                }
                Intent i = new Intent(mContext, NotifyService.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("time", notifyList);
                i.putExtra(Config.NOTIFY_TODAY,today);
                i.putExtras(bundle);
                mContext.startService(i);
                break;
        }
    }
}