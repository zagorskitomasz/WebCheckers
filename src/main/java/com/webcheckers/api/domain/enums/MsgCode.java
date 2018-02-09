package com.webcheckers.api.domain.enums;

import java.security.InvalidParameterException;

public enum MsgCode {

	CREATE_GAME("01"),
	JOIN_GAME("02"),
	GAME_CREATED("03"),
	GAME_EXISTS("04"),
	GAME_STARTED("05"),
	GAME_FINISHED("06"),
	GAME_FULL("07"),
	
	CLICKED_FIELD("11"),
	CHECKER_SELECTED("12"),
	CHECKER_UNSELECTED("13"),
	CHECKER_TO_KILL("14"),
	CHECKER_ON_FIELD("15"),
	CHECKER_OFF_FIELD("16"),
	INVALID_MOVE("17"),
	
	YOUR_MOVE("20"),
	YOU_WON("21"),
	YOU_LOST("22"),
	DRAW("23"),
	BLACK_WON("24"),
	WHITE_WON("25"),
	
	ERROR("30"),
	TIME_OUT("31");
	
	private String code;
	
	private MsgCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static MsgCode getByCode(String code) {
		
		for(MsgCode msgCode : MsgCode.values()) {
			if(code.equals(msgCode.getCode()))
				return msgCode;
		}
		throw new InvalidParameterException("String code unknown.");
	}
}
