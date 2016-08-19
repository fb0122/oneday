package oneday.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.fb0122.oneday.R;
import com.example.fb0122.oneday.utils.TimeCalendar;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by fb0122 on 2016/5/4.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    NotificationCompat.Builder builder;
    Context mContext;
    NotificationManager manager;
    int notifyId = 10;
    String nowTime;
    private static Bundle bundle = new Bundle();

    private static String week;
    private static String hour;
    private static String minute;
    private ArrayList<String> timeList = new ArrayList<String>();

    public static void setWeek(String week) {
        AlarmReceiver.week = week;
    }

    public static String getWeek() {
        return week;
    }

    public static void setHour(String hour) {
        AlarmReceiver.hour = hour;
    }

    public static String getHour() {
        return hour;
    }

    public static void setMinute(String minute) {
        AlarmReceiver.minute = minute;
    }

    public static String getMinute() {
        return minute;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (intent.getAction().equals("NotifyService.Intent") && intent != null ) {
            timeList = intent.getStringArrayListExtra("data");
            getTime();
            if (Integer.parseInt(getMinute()) < 10){
                nowTime = getHour() + ":0" + getMinute();
            }else {
                nowTime = getHour() + ":" + getMinute();
            }
            for (int i = 0;i<timeList.size();i++){
                if (nowTime.equals(timeList.get(i))){
                    createNotify();
                }
            }
            intent.setClass(mContext,NotifyService.class);
            bundle.putStringArrayList("time",timeList);
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }
    //通知栏
    public  void createNotify(){
        manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.notifycation);
        views.setTextViewText(R.id.notifyTitle,"testNotify");
        builder = new NotificationCompat.Builder(mContext);
        builder .setContent(views)
                .setWhen(System.currentTimeMillis())
                .setTicker("oneday tips")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setSmallIcon(R.drawable.onedaylogo1);

        Notification notify = builder.build();
        notify.contentView = views;
        notify.defaults = Notification.DEFAULT_ALL;
        manager.notify(notifyId,notify);
    }

    public  PendingIntent getDefaultIntent(int flag){
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1,new Intent(),flag);
        return  pendingIntent;
    }

    public static void getTime(){
        Calendar c = Calendar.getInstance();
        hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        minute = String.valueOf(c.get(Calendar.MINUTE));
        setWeek(TimeCalendar.getTodayWeek());
        setHour(hour);
        setMinute(minute);
    }
}
