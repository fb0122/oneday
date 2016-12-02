package oneday.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.example.fb0122.oneday.MainActivity;
import com.example.fb0122.oneday.R;

import db_oneday.OneDaydb;


public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;
    private Bundle bundle = new Bundle();
    private int totalNotifies;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (intent != null && intent.getAction().equals(NotifyService.NOTIFY_ACTION)) {
            totalNotifies = intent.getIntExtra(Config.NOTIFY_NUMBER,1);
            createNotify(intent.getStringExtra("nowTime"));
        }
        if (intent != null && intent.getAction().equals(NotifyService.SERVICE_DESTROY_ACTION)){
            intent.setClass(mContext,NotifyService.class);
            bundle.putStringArrayList("time", intent.getStringArrayListExtra("timeList"));
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }

    //通知栏
    public  void createNotify(String fromTime){
        OneDaydb oneDaydb = new OneDaydb(mContext, OneDaydb.TABLE_NAME);
        NotificationManager manager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.notifycation);

        if (totalNotifies == 1) {
            String[] str = oneDaydb.getNotifyInfo(fromTime);
            views.setViewVisibility(R.id.text_notify_number_tips, View.GONE);
            views.setViewVisibility(R.id.text_notify_time, View.VISIBLE);
            views.setViewVisibility(R.id.text_notify_custom, View.VISIBLE);
            views.setTextViewText(R.id.text_notify_time, fromTime + " - " + str[0]);
            views.setTextViewText(R.id.text_notify_custom, str[1]);
        }else{
            views.setViewVisibility(R.id.text_notify_number_tips, View.VISIBLE);
            views.setViewVisibility(R.id.text_notify_time, View.GONE);
            views.setViewVisibility(R.id.text_notify_custom, View.GONE);
            views.setTextViewText(R.id.text_notify_number_tips,"您今天有" + totalNotifies + "个计划未完成");
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder .setContent(views)
                .setContentIntent(pendingIntent)
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
        int notifyId = 10;
        manager.notify(notifyId,notify);
    }

}
