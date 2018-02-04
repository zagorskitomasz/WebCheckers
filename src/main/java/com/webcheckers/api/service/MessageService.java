package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

public interface MessageService {

	public void resolveMessage(WebSocketSession session, String longMessage);
}
