package com.agogesmsgateway.service;

import android.content.Context;

import com.agogesmsgateway.model.Command;
import com.agogesmsgateway.model.SMSCommand;
import com.agogesmsgateway.worker.SMSSender;

public class SMSService extends CommandService {
	
	public static final String COMMAND_SERVICE = "SMS";
	
	private SMSCommand command;
	private Context context;
	
	public SMSService(Command command, Context context) {
		
		if(command instanceof SMSCommand) {
			this.command = (SMSCommand)command;
			this.context = context;
		}
		else
			throw new UnsupportedOperationException("Command must be SMSCommand");
	}

	@Override
	public void start() {
		SMSSender sender = new SMSSender(context, command);		
		sender.sendSms();
	}
}
