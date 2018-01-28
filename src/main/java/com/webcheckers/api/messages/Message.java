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
		
		appendCode(stringBuilder);
		appendArgs(stringBuilder);
		
		return stringBuilder.toString().trim();
	}

	private void appendCode(StringBuilder stringBuilder) {
		
		stringBuilder.append(CODE.getCode() + " ");
	}

	private void appendArgs(StringBuilder stringBuilder) {
		
		for(int i = 0; i < ARGS.length ; i++)
			stringBuilder.append(ARGS[i] + " ");
	}
	
	public static Message deserialize(String stringMessage) {
		
		try {
			String[] splittedStrings = stringMessage.split(" ");
			
			MsgCode code = readCode(splittedStrings);
			String[] args = readArgs(splittedStrings);
			
			Message message = new Message(code, args);
			
			return message;
		}
		catch(Exception ex) {
			throw new InvalidParameterException("Deserialization failed");
		}
	}

	private static MsgCode readCode(String[] splittedStrings) {
		
		MsgCode code = MsgCode.getByCode(splittedStrings[0]);
		
		return code;
	}

	private static String[] readArgs(String[] splittedStrings) {
		
		String[] args = new String[splittedStrings.length - 1];
		
		for(int i = 1; i < splittedStrings.length; i++)
			args[i-1] = splittedStrings[i];
		
		return args;
	}
}
