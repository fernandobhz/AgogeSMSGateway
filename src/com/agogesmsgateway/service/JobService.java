package com.agogesmsgateway.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.agogesmsgateway.model.Command;
import com.agogesmsgateway.receiver.SMSResultReceiver;
import com.agogesmsgateway.utils.Const;
import com.agogesmsgateway.utils.Utils;
import com.agogesmsgateway.worker.SMSSender;

public class JobService extends Service {

	private static final String KEY_LAST_MSG = "key_last_msg";
	private static final String NAME = "com.notifierapplication.JobService";

	private static volatile PowerManager.WakeLock lockStatic = null;

	synchronized private static PowerManager.WakeLock getLock(Context context) {

		if (lockStatic == null) {

			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
			lockStatic.setReferenceCounted(true);
		}

		return (lockStatic);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("info", "servce onstartcommand");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0)
			lock.acquire();

		doWakefulWork(intent);

		super.onStartCommand(intent, flags, startId);

		return (START_REDELIVER_INTENT);
	}

	protected void doWakefulWork(Intent intent) {

		//Utils.stopAlarmManager(this);
		
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals(SMSSender.SMS_SEND_INTENT)) {
			
			new ResultSender(intent.getExtras().getInt(SMSResultReceiver.KEY_RESULT), intent.getExtras().getInt(Command.KEY_COMMAND_ID)).execute();
			
			Log.d("info", "job service callback");
			
		} else {
			Log.d("info", "job service clear");
			new NetworkWorker().execute();
		}
	}

	public void freeLock() {

		Log.d("info", "free log called");

		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (lock.isHeld())
			lock.release();
		
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
	
	public String getLastMessage() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getString(KEY_LAST_MSG, "");
	}

	public void putLastMessage(String msg) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();

		editor.putString(KEY_LAST_MSG, msg);
		editor.commit();
	}

	private class ResultSender extends AsyncTask<Void, Void, Void> {
		
		private int result;
		private int commandId;
		
		public ResultSender(int result, int commandId) {
			this.result = result;
			this.commandId = commandId;
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(Const.SERVICE_URL+"?"+Const.COMMAND_ID_PARAMETER+commandId+"&"+Const.RESULT_PARAMETER+result);
				
				httpclient.execute(httpGet);
				Log.d("info", "send request: " + httpGet.getURI().toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			//Utils.runAlarmManager(JobService.this);
			//Utils.startService(JobService.this);
			
			freeLock();
		}
	}

	private class NetworkWorker extends AsyncTask<Void, Void, Void> {
		
		private boolean smsSent = false;

		@Override
		protected Void doInBackground(Void... params) {

			try {

				Log.d("info", "networkWorker has started");

				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(Const.SERVICE_URL
						+ Const.DEVICE_ID_PARAMETER
						+ Utils.getIMEI(JobService.this));

				HttpResponse response = httpclient.execute(httpGet);

				if (response != null && response.getEntity() != null) {

					String responseBody = EntityUtils.toString(response
							.getEntity());

					if (!responseBody.equals(Const.DEFAULT_SERVER_RESPONSE) && !getLastMessage().equals(responseBody)) {
						
						Log.e("sending sms", "sms body: ["+responseBody+"], last message: ["+getLastMessage()+"]");

						putLastMessage(responseBody);
						smsSent = true;
						
						Command command = Command.createFromCommand(responseBody);								
						CommandService.createFromCommand(JobService.this, command).start();

						// sendNotification(responseBody);
					}
					else {
						Log.d("info", "passed, already sent");
					}

				} else
					Log.d("info", "response is null");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			//if(!smsSent)
				//Utils.runAlarmManager(JobService.this);
			
			freeLock();
		}
	}
}
