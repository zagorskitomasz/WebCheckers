package com.webcheckers.api.domain.moves;

public class Position {

	public final int X;
	public final int Y;
	
	public Position(int X, int Y) {
		
		this.X = X;
		this.Y = Y;
	}
	
	@Override
	public boolean equals(Object other) {
		
		Position otherPosition = (Position)other;
		
		return this.X == otherPosition.X && this.Y == otherPosition.Y;
	}
	
	@Override
	public String toString() {
		return X + " " + Y;
	}
}
