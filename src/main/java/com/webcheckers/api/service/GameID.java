package com.webcheckers.api.service;

public class GameID {
	
	public final String NAME;
	public final String PASSWORD;
	private long lastCheck;
	
	public GameID(String name, String password) {
		
		NAME = name;
		PASSWORD = password;
		lastCheck = System.currentTimeMillis();
	}
	
	public boolean isTimedOut() {
		
		return System.currentTimeMillis() > lastCheck + GameDestroyer.TIME_OUT_VALUE;
	}
	
	private void check() {
		
		lastCheck = System.currentTimeMillis();
	}
	
	@Override
	public boolean equals(Object object) {
		
		GameID otherID = (GameID) object;
		
		if(NAME.equals(otherID.NAME) && PASSWORD.equals(otherID.PASSWORD)){
			
			check();
			otherID.check();
			
			return true;
		}
		return false;
	}
}
