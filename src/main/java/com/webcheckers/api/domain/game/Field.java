package com.webcheckers.api.domain.game;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.moves.Position;

public class Field {

	public final Position POSITION;
	private Checker checker;
	
	public Field(int x, int y) {
		POSITION = new Position(x, y);
		checker = null;
	}
	
	public void insertChecker(Checker checker) {
		this.checker = checker;
	}
	
	public void removeChecker() {
		checker = null;
	}
	
	public boolean hasChecker() {
		return checker != null;
	}
	
	public Checker getChecker() {
		return checker;
	}
	
	@Override
	public String toString() {
		
		if(checker == null)
			return "_";
		else if (checker.COLOR == Color.BLACK) {
			if(checker.isPromoted())
				return "B";
			else
				return "b";
		}
		else if (checker.COLOR == Color.WHITE) {
			if(checker.isPromoted())
				return "W";
			else
				return "w";
		}
		else
			return " ";
	}
}
