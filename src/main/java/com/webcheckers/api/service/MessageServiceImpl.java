package com.webcheckers.api.service;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.messages.Message;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private GameService gameService;
	
	@Override
	public void resolveMessage(WebSocketSession session, String longMessage) {
		
		try {
			Message message = Message.deserialize(longMessage);
			resolveMessage(session, message);
		}
		catch(Exception ex) {
			Logger.getGlobal().warning(longMessage);
			ex.printStackTrace();
		}
	}
	
	private void resolveMessage(WebSocketSession session, Message message) {
		
		switch(message.CODE) {
		case CREATE_GAME:
			createGame(session, message);
			break;
		case JOIN_GAME:
			joinGame(session, message);
			break;
		case DESTROY_GAME:
			destroyGame(session, message);
			break;
		case CLICKED_FIELD:
			clickedField(session, message);
			break;
		default:
			break;
		
		}
	}
	
	private void createGame(WebSocketSession session, Message message) throws IOException {
		
		Player player = new Player("White");
		player.initialize(Color.WHITE, session);
		
		GameID gameID = message.gameID;
		
		MsgCode resultCode = gameService.createGame(message.gameID, player);
		Message response = new Message(resultCode, message.gameID, (String[])null);
		
		notifyBoth(gameID, response);
	}
	
	private void notifyBoth(GameID gameID, Message response) throws IOException {
		
		Player[] players = gameService.getPlayers(gameID);
		
		if(players == null)
			return;
		
		TextMessage textMessage = new TextMessage(response.serialize());
		
		for(Player player : players) {
			if(player != null && player.getWsSession() != null) {
				
				WebSocketSession session = player.getWsSession();
				session.sendMessage(textMessage);
			}
		}
	}
}
