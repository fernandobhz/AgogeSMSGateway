package com.agogesmsgateway.service;

import android.content.Context;

import com.agogesmsgateway.model.Command;
import com.agogesmsgateway.model.SMSCommand;

@SuppressWarnings("unused")
public abstract class CommandService {
			
	public static CommandService createFromCommand(Context context, Command command) {
		
		if(command.getService().equals(SMSService.COMMAND_SERVICE))
			return new SMSService(command, context);
		else
			throw new UnsupportedOperationException("Can't find an exact CommandService for given command");		
	}
		
	public abstract void start();
}
