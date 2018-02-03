package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.messages.Message;

public interface MessageService {

	public void registerPlayer(WebSocketSession session);
	public void resolveMessage(Message message);
}
