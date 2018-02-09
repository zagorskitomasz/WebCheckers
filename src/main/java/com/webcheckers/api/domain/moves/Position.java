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
		return X + "$" + Y;
	}

	public static Position parse(String positionString) {
		String[] coords = positionString.split("$");
		
		int x = Integer.parseInt(coords[0]);
		int y = Integer.parseInt(coords[1]);
		
		Position position = new Position(x, y);
		return position;
	}
}
