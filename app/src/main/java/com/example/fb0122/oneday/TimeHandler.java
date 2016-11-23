package com.example.fb0122.oneday;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import db_oneday.OneDaydb;
import oneday.Alarm.Config;
import oneday.Alarm.NotifyService;


public class TimeHandler extends Handler {

    private OneDaydb db = new OneDaydb(MainActivity.mContext, OneDaydb.TABLE_NAME);
    Cursor c;
    private SQLiteDatabase dbreader;
    private String time;

    public TimeHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Config.ADD_NOTIFY:
                dbreader = db.getReadableDatabase();
                c = db.Query();
                if (c.moveToFirst()) {
                    do {

                        time = c.getString(c.getColumnIndex(OneDaydb.COLUMN_FROM_TIME));
                        MainActivity.notiifyList.add(time);
                    } while (c.moveToNext());
                }
                Intent i = new Intent(MainActivity.mContext, NotifyService.class);
                i.setAction("NotifyService.Intent" + "");
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("time", MainActivity.notiifyList);
                i.putExtras(bundle);
                MainActivity.mContext.startService(i);
                break;
        }
    }
}