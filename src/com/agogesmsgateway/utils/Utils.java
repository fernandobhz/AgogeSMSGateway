package com.agogesmsgateway.utils;

import java.util.Random;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.agogesmsgateway.R;
import com.agogesmsgateway.service.JobService;

public class Utils {

	public static void runAlarmManager(Context context) {

		Intent intent = new Intent(context, JobService.class);
		@SuppressWarnings("unused")
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, 0);

		@SuppressWarnings("unused")
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), Const.UPDATE_INTERVAL * 1000,
			pendingIntent);
	}

	public static void stopAlarmManager(Context context) {

		Intent intent = new Intent(context, JobService.class);
		@SuppressWarnings("unused")
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, 0);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	public static void startService(final Context context) {

		Handler mHandler = new Handler();

		Runnable mUpdateTimeTask = new Runnable() {
			
			public void run() {
				context.startService(new Intent(context, JobService.class));
			}
		};
		
		mHandler.postDelayed(mUpdateTimeTask, Const.UPDATE_INTERVAL*1000);
	}

	public static int getRandom() {

		Random rand = new Random();
		int randomNum = rand.nextInt(99999 - 10000 + 1) + 10000;

		return randomNum;
	}

	public static String getIMEI(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static void sendNotification(Context context, String msg) {

		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		builder.setTicker("You got a new message")
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setAutoCancel(true)
				.setContentTitle(
						context.getResources().getString(R.string.app_name))
				.setDefaults(
						Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE)
				.setContentText(msg);

		Notification notification = builder.build();
		manager.notify(Utils.getRandom(), notification);
	}
}
