package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.messages.Message;

public interface MessageService {

	public void resolveMessage(WebSocketSession session, String longMessage);
	public void notifyBoth(GameID gameID, Message response);
}
