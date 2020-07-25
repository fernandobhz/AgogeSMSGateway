package com.agogesmsgateway.model;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

public class SMSCommand extends Command {
	
	public static final String COMMAND_TYPE = "SMS";
	
	private String number;
	private String text;
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static SMSCommand createFromString(String command) {
		
		if(command == null || command.length() ==0)
			throw new InvalidParameterException("Parameter value must not be null or 0-sized");
		
		SMSCommand result = new SMSCommand();
		List<String> items = Arrays.asList(command.split("\\s*:\\s*"));
		
		if(items.size() !=4)
			throw new InvalidParameterException("Unexpected parameter count: expected: 4, got: " + items.size());
		
		result.setService(items.get(0));
		result.setId(Integer.valueOf(items.get(1)));
		result.setNumber(items.get(2));
		result.setText(items.get(3));
		
		return result;		
	}
}
