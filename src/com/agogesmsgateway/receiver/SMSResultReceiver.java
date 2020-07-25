package com.agogesmsgateway.receiver;

import com.agogesmsgateway.model.Command;
import com.agogesmsgateway.service.JobService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSResultReceiver extends BroadcastReceiver {
	
	public static final String KEY_RESULT = "key_result";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("info", "sms intent action: "+intent.getAction() + ", command id: " + intent.getExtras().getInt(Command.KEY_COMMAND_ID));
		context.unregisterReceiver(this);
		
		Intent serviceIntent = new Intent(context, JobService.class);
		serviceIntent.setAction(intent.getAction());
		serviceIntent.putExtra(KEY_RESULT, getResultCode() == Activity.RESULT_OK ? 1 : 0);
		serviceIntent.putExtra(Command.KEY_COMMAND_ID, intent.getExtras().getInt(Command.KEY_COMMAND_ID));
		
		switch (getResultCode()) {

		case Activity.RESULT_OK:

			Log.d("log", "sms result ok");
			
			break;

		default:

			Log.d("log", "sms result not ok");
			break;
		}
		
		context.startService(serviceIntent);
	}
}
