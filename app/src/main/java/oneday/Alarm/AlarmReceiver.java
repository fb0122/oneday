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

import com.example.fb0122.oneday.MainActivity;
import com.example.fb0122.oneday.R;
import com.example.fb0122.oneday.utils.TimeCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import db_oneday.OneDaydb;


public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;
    private int notifyId = 10;
    private String nowTime;
    private Bundle bundle = new Bundle();
    private OneDaydb oneDaydb;

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
        if (intent != null && intent.getAction().equals("NotifyService.Intent")) {
            timeList = intent.getStringArrayListExtra("data");
            getTime();
            if (Integer.parseInt(getMinute()) < 10){
                nowTime = getHour() + ":0" + getMinute();
            }else {
                nowTime = getHour() + ":" + getMinute();
            }
            for (int i = 0;i<timeList.size();i++){
                if (nowTime.equals(timeList.get(i))){
                    createNotify(nowTime);
                    timeList.remove(nowTime);
                }
            }
            intent.setClass(mContext,NotifyService.class);
            bundle.putStringArrayList("time",timeList);
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }

    //通知栏
    public  void createNotify(String fromTime){
        oneDaydb = new OneDaydb(mContext,OneDaydb.TABLE_NAME);              //获取习惯与时间段数据,并显示在notifytion中
        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.notifycation);
        String[] str = oneDaydb.getNotifyInfo(fromTime);
        views.setTextViewText(R.id.text_notify_time,fromTime + " - " + str[0]);
        views.setTextViewText(R.id.text_notify_custom,str[1]);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder .setContent(views)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setWhen(System.currentTimeMillis())
                .setTicker("Tips")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.onedaylogo1);

        Notification notify = builder.build();
        notify.contentView = views;
        notify.defaults = Notification.DEFAULT_ALL;
        manager.notify(notifyId,notify);
    }

    public  PendingIntent getDefaultIntent(int flag){
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,1,intent,flag);
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
