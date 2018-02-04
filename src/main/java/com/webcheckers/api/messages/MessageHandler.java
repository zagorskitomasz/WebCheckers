package com.webcheckers.api.messages;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.webcheckers.api.service.MessageService;

@Component
public class MessageHandler extends AbstractWebSocketHandler{
	
	private MessageService messageService;
	
	public MessageHandler(MessageService messageService) {
		this.messageService = messageService;
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		
		messageService.resolveMessage(session, message.getPayload());
	}
}
