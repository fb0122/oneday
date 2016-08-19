package oneday.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by fb0122 on 2016/5/4.
 */
public class NotifyService extends Service{

    private static final String TAG = "NotifyService";

    ArrayList<String> list = new ArrayList<>();
    String action = "NotifyService.Intent";
    private static int iflag = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(iflag == 0 ? (intent.getAction() != null ) : (intent.getAction().equals("NotifyService.Intent")) ){
            list = intent.getStringArrayListExtra("time");
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            long tiggerAtTime = SystemClock.elapsedRealtime();

            Intent i = new Intent(getApplicationContext(),AlarmReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("data",list);
            i.putExtras(bundle);
            i.setAction(action);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,i,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,tiggerAtTime,pendingIntent);

            iflag += 1;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
