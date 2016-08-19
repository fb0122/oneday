package com.example.fb0122.oneday;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class AtyNotification extends Activity {

    WakeLock mWakelock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atynotification);

        Toast.makeText(this, "successed", Toast.LENGTH_SHORT).show();

        Log.e("i am your activity", "");

        final Window win = getWindow();
        win.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );


    }

    @Override
    protected void onResume() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWakelock.release();
        super.onPause();
    }

}
