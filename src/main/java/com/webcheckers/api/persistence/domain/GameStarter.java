package com.webcheckers.api.persistence.domain;

import java.io.Serializable;

public class GameStarter implements Serializable{

	private static final long serialVersionUID = -6368097393476240820L;

	private int id;
	
	public GameStarter(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
