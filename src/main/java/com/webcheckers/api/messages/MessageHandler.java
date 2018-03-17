package com.webcheckers.api.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.service.MessageService;

@Component
public class MessageHandler extends AbstractWebSocketHandler{
	
	@Autowired
	private MessageService messageService;
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		messageService.resolveMessage(session, message.getPayload());
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		
		messageService.notifySessionUser(null, session, MsgCode.CONNECTED);
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		
		messageService.sessionClosed(session);
	}
}
