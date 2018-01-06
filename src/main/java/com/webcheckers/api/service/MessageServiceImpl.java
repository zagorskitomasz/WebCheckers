package com.webcheckers.api.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class MessageServiceImpl implements MessageService {

	private WebSocketSession session;
	
	@Override
	public void addSession(WebSocketSession session) {
		
		this.session = session;
	}

	@Override
	public void sendMessage() {
		try {
			session.sendMessage(new TextMessage("Hello!", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
