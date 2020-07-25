package com.agogesmsgateway.worker;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import com.agogesmsgateway.model.Command;
import com.agogesmsgateway.model.SMSCommand;
import com.agogesmsgateway.receiver.SMSResultReceiver;

public class SMSSender {

	public static final String SMS_SEND_INTENT = "sms_send_intent";
	
	public static final int SMS_RESULT_SUCCESSFUL = 1;
	public static final int SMS_RESULT_UNSUCCESSFUL = 0;

	private SMSCommand command;
	private Context context;

	public SMSSender(Context context, SMSCommand command) {
		this.command = command;
		this.context = context;
	}

	public void sendSms() { 
     
		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(command.getText());
		
		Intent broadcastIntent = new Intent(SMS_SEND_INTENT);
		broadcastIntent.putExtra(Command.KEY_COMMAND_ID, command.getId());

		PendingIntent intent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
		
		ArrayList<PendingIntent> intentList = new ArrayList<PendingIntent>();
		intentList.add(intent);

		context.registerReceiver(new SMSResultReceiver(), new IntentFilter(SMS_SEND_INTENT));
		sms.sendMultipartTextMessage(command.getNumber(), null, parts, intentList, null);
		 
		Log.d("Info", "Sent sms with text [" + command.getText() + "], to phone: [" + command.getNumber() + "], command ID: ["+command.getId()+"]");
	}	
}
