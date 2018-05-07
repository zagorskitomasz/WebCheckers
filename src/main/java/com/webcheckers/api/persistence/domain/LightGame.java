package com.webcheckers.api.persistence.domain;

import java.io.Serializable;

public class LightGame implements Serializable{

	private static final long serialVersionUID = -7155100990589809221L;
	
	private int id;
	private String state;
	private String winner;

	public LightGame() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public void setWinner(String winner) {
		this.winner = winner;
	}
}
