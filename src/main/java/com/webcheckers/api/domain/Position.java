package com.webcheckers.api.domain;

public class Position {

	public final int X;
	public final int Y;
	private Checker checker;
	
	public Position(int x, int y) {
		this.X = x;
		this.Y = y;
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
		else if (checker.COLOR == Color.BLACK)
			return "B";
		else if (checker.COLOR == Color.WHITE)
			return "W";
		else
			return " ";
	}
}
