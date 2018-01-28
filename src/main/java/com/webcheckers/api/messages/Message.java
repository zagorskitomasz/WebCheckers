package com.webcheckers.api.messages;

import java.io.Serializable;
import java.security.InvalidParameterException;

import com.webcheckers.api.domain.enums.MsgCode;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;

	public final MsgCode CODE;
	public final String[] ARGS;
	
	public Message(MsgCode code, String...args) {
		this.CODE = code;
		this.ARGS = args;
	}
	
	public Message(MsgCode code) {
		this.CODE = code;
		this.ARGS = new String[0];
	}
	
	public String serialize() {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CODE.getCode() + " ");
		
		for(int i = 0; i < ARGS.length ; i++)
			stringBuilder.append(ARGS[i] + " ");
		
		return stringBuilder.toString().trim();
	}
	
	public static Message deserialize(String stringMessage) {
		
		try {
			String[] splittedStrings = stringMessage.split(" ");
			
			MsgCode code = MsgCode.getByCode(splittedStrings[0]);
			
			String[] args = new String[splittedStrings.length - 1];
			
			for(int i = 1; i < splittedStrings.length; i++)
				args[i-1] = splittedStrings[i];
			
			Message message = new Message(code, args);
			
			return message;
		}
		catch(Exception ex) {
			throw new InvalidParameterException("Deserialization failed");
		}
	}
}
