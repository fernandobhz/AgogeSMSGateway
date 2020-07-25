package com.agogesmsgateway.model;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;


public abstract class Command {
	
	public static final String KEY_COMMAND_ID = "key_command_id";
	
	private int id;
	private String service;
	
	public static Command createFromCommand(String command) {
		
		if(command == null || command.length() ==0 || !command.contains(":"))
			throw new InvalidParameterException("Parameter value must not be null or 0-sized");
		
		List<String> items = Arrays.asList(command.split("\\s*:\\s*"));
		String commandType = items.get(0);
		
		if(commandType.equals(SMSCommand.COMMAND_TYPE))
			return SMSCommand.createFromString(command);
		/*
		 * else if other command types classes
		 */
		else 
			throw new UnsupportedOperationException("The command cannot be represented to any known Command realisation");
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
}
