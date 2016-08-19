package com.example.fb0122.oneday;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverNo extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.e("ReceiverNo", "");
		
		KeyguardManager key = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
		if (key.inKeyguardRestrictedInputMode()) {
			
			Intent	alarmintent = new Intent(context,AtyNotification.class);
			alarmintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(alarmintent);
		}
	}
	
}
