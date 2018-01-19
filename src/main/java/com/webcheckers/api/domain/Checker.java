package com.webcheckers.api.domain;

public class Checker {

	public final Color COLOR;
	private Position position;
	private boolean promoted;
	
	public Checker(Color color, Position position) {
		this.COLOR = color;
		this.position = position;
		promoted = false;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void moveTo(Position position) {
		this.position.removeChecker();
		position.insertChecker(this);
		this.position = position;
	}
	
	public void promote() {
		promoted = true;
	}
	
	public boolean isPromoted() {
		return promoted;
	}
}
