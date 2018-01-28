package com.webcheckers.api.domain.game;

import com.webcheckers.api.domain.enums.Color;

public class Checker {

	public final Color COLOR;
	private Field field;
	private boolean promoted;
	
	public Checker(Color color, Field field) {
		this.COLOR = color;
		this.field = field;
		promoted = false;
	}
	
	public Field getField() {
		return field;
	}
	
	public void moveTo(Field field) {
		this.field.removeChecker();
		field.insertChecker(this);
		this.field = field;
	}
	
	public void promote() {
		promoted = true;
	}
	
	public boolean isPromoted() {
		return promoted;
	}
}
