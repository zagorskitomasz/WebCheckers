package com.webcheckers.api.service;

public class GameID {

	public final String NAME;
	public final String PASSWORD;
	
	public GameID(String name, String password) {
		
		NAME = name;
		PASSWORD = password;
	}
	
	@Override
	public boolean equals(Object object) {
		
		GameID otherID = (GameID) object;
		
		return this.NAME.equals(otherID.NAME) && this.PASSWORD.equals(otherID.PASSWORD);
	}
}
