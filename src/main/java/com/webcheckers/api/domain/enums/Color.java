package com.webcheckers.api.domain.enums;

public enum Color {
	
	WHITE("W"),
	BLACK("B");
	
	private String symbol;
	
	private Color(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public static Color getColorBySymbol(String symbol) {
		
		for (Color color : Color.values()) {
			if(color.symbol.equals(symbol))
				return color;
		}
		return null;
	}
}
