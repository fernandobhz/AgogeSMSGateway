package com.agogesmsgateway;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.agogesmsgateway.utils.Utils;

public class RunActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		
		super.onCreate(bundle);
		Utils.runAlarmManager(this);
		//Utils.startService(this);
		
		Toast.makeText(this, "Monitoring service has been started", Toast.LENGTH_LONG).show();
		finish();
	}
}
