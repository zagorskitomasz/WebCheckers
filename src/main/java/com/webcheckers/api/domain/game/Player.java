package com.webcheckers.api.domain.game;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.Color;

public class Player {

	private WebSocketSession wsSession;
	private Color color;
	private String name;

	public Player(String name) {
		this.name = name;
	}

	public void initialize(Color color, WebSocketSession wsSession) {
		this.color = color;
		this.wsSession = wsSession;
	}

	public WebSocketSession getWsSession() {
		return wsSession;
	}

	public void setWsSession(WebSocketSession wsSession) {
		this.wsSession = wsSession;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void invert() {
		
		color = color == Color.BLACK ? Color.WHITE : Color.BLACK; 
	}
}
