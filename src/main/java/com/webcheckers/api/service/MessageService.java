package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.messages.Message;

public interface MessageService {

	public void resolveMessage(WebSocketSession session, String longMessage);
	public void notifyBoth(Message response);
	public void notifySessionUser(GameID gameID, WebSocketSession session, MsgCode resultCode);
	public void sessionClosed(WebSocketSession session);
}
