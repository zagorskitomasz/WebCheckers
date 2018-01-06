package com.webcheckers.api.messages;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.webcheckers.api.service.MessageService;

@Component
public class MovesNotificationer extends AbstractWebSocketHandler{
	
	private MessageService messageService;
	
	public MovesNotificationer(MessageService messageService) {
		this.messageService = messageService;
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session){
		messageService.addSession(session);
	}
}
