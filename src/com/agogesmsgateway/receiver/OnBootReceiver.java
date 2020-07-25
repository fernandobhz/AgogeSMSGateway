package com.agogesmsgateway.receiver;

import com.agogesmsgateway.service.JobService;
import com.agogesmsgateway.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

@SuppressWarnings("unused")
public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		
		Log.d("info", "on receive");
		
		Utils.runAlarmManager(context);
		
		//context.startService(new Intent(context, JobService.class));
	}
}
