package com.webcheckers.api.domain.game;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.Color;

public class Player {

	private WebSocketSession wsSession;
	private boolean active;
	private Color color;
	private String name;

	public Player(String name) {
		this.name = name;
	}

	public void initialize(Color color, WebSocketSession wsSession) {
		this.color = color;
		this.wsSession = wsSession;

		initializeActivity(color);
	}

	private void initializeActivity(Color color) {
		if (color == Color.WHITE)
			setActive(true);
		else
			setActive(false);
	}

	public WebSocketSession getWsSession() {
		return wsSession;
	}

	public void setWsSession(WebSocketSession wsSession) {
		this.wsSession = wsSession;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void switchActive() {
		active = !active;
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
}
