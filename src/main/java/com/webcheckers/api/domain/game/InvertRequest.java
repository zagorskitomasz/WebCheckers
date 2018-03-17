package com.webcheckers.api.domain.game;

import java.util.HashMap;
import java.util.Map;

import com.webcheckers.api.domain.enums.Color;

public class InvertRequest {

	private Map<Color, Boolean> request;
	
	public InvertRequest() {
		
		request = new HashMap<>();
		reset();
	}
	
	public void addPlayer(Color color) {
		request.put(color, true);
	}
	
	public boolean isCompleted() {
		return request.get(Color.WHITE) && request.get(Color.BLACK);
	}
	
	public void reset() {
		
		request.put(Color.WHITE, false);
		request.put(Color.BLACK, false);
	}
}
