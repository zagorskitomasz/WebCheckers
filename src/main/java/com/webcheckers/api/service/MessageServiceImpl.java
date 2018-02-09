package com.webcheckers.api.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.moves.Position;
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
			System.out.println("Deserializing failed: " + longMessage);
			ex.printStackTrace();
		}
	}
	
	private void resolveMessage(WebSocketSession session, Message message) throws IOException {
		
		switch(message.CODE) {
		case CREATE_GAME:
			createGame(session, message);
			break;
		case JOIN_GAME:
			joinGame(session, message);
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
		
		MsgCode resultCode = gameService.createGame(message.gameID, player);
		Message response = new Message(resultCode, message.gameID, (String[])null);
		
		notifyBoth(response);
	}
	
	private void joinGame(WebSocketSession session, Message message) throws IOException {
		
		Player player = new Player("Black");
		player.initialize(Color.BLACK, session);
		
		GameID gameID = message.gameID;
		
		MsgCode resultCode = gameService.joinGame(gameID, player);
		notifyBothNoArgsCode(gameID, resultCode);
		
		if(resultCode == MsgCode.GAME_STARTED) {
			sendCheckersToAdd(gameID);
			sendWhoseMove(gameID);
		}
		
	}
	
	private void clickedField(WebSocketSession session, Message message) {
		
		GameID gameID = message.gameID;
		Position position = Position.parse(message.ARGS[0]);
		
		MoveResult result = gameService.move(gameID, position);
		dispatchMoveResult(gameID, result);
	}

	private void dispatchMoveResult(GameID gameID, MoveResult result) {
		
		switch(result) {
		case GAME_OVER:
			gameOver(gameID);
			break;
		case MOVE_COMPLETED:
			moveCompleted(gameID);
			break;
		case MOVE_INITIALIZED:
			moveInitialized(gameID);
			break;
		case MOVE_IN_PROGRESS:
			moveInProgress(gameID);
			break;
		case MOVE_REJECTED:
			moveRejected(gameID);
			break;
		default:
			break;
		}
	}
	
	private void gameOver(GameID gameID){
		
		moveCompleted(gameID);
		
		Color winnerColor = whoWon(gameID);
		
		for(Player player : gameService.getPlayers(gameID)) {
			if(player.getColor() == winnerColor)
				notifyOneNoArgsCode(gameID, player, MsgCode.YOU_WON);
			else
				notifyOneNoArgsCode(gameID, player, MsgCode.YOU_LOST);
		}
		
	}

	private Color whoWon(GameID gameID) {
		MsgCode whoWon = gameService.whoWon(gameID);
		Color winnerColor = whoWon == MsgCode.BLACK_WON ? Color.BLACK : Color.WHITE;
		return winnerColor;
	}

	private void notifyBothNoArgsCode(GameID gameID, MsgCode code) {
		
		Message message = new Message(code, gameID, (String[])null);
		notifyBoth(message);
	}

	private void notifyOneNoArgsCode(GameID gameID, Player player, MsgCode code) {
		
		Message message = new Message(code, gameID, (String[])null);
		notifyPlayer(player, message);
	}
	
	private void sendCheckersToAdd(GameID gameID) {
		
		String[] checkers = gameService.getCheckersToAdd(gameID);
		Message message = new Message(MsgCode.CHECKER_ON_FIELD, gameID, checkers);
		
		notifyBoth(message);
	}
	
	private void sendWhoseMove(GameID gameID) {
		
		Player player = gameService.whoseMove(gameID);
		notifyOneNoArgsCode(gameID, player, MsgCode.YOUR_MOVE);
	}
	
	@Override
	public void notifyBoth(Message response){
		
		GameID gameID = response.gameID;
		Player[] players = gameService.getPlayers(gameID);
		
		if(players == null)
			return;
		
		for(Player player : players) {
			notifyPlayer(player, response);
		}
	}

	private void notifyPlayer(Player player, Message response) {
		
		if(player != null && player.getWsSession() != null) {

			TextMessage textMessage = new TextMessage(response.serialize());
			WebSocketSession session = player.getWsSession();
			
			try {
				session.sendMessage(textMessage);
			} 
			catch (IOException ex) {
				System.out.println("Sending message failed: " + textMessage.getPayload());
				ex.printStackTrace();
			}
		}
	}
}
