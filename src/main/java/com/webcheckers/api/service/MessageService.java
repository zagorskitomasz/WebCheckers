package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

public interface MessageService {

	public void addSession(WebSocketSession session);
	public void sendMessage();
}