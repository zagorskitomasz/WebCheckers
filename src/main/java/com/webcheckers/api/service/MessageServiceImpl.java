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
	public void sessionClosed(WebSocketSession session) {
		
		Player waiting = gameService.playerDisconnected(session);
		
		if(waiting == null)
			return;
		
		Message message = new Message(MsgCode.PLAYER_DISCONNECTED, null, waiting.getColor() == Color.WHITE ? "b" : "w");
		notifyPlayer(waiting, message);
	}
	
	@Override
	public void resolveMessage(WebSocketSession session, String longMessage) {
		
		try {
			Message message = Message.deserialize(longMessage);
			
			if(message != null)
				resolveMessage(session, message);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Deserialization failed: " + longMessage);
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
		case INVERT:
			invert(session, message);
			break;
		default:
			break;
		
		}
	}
	
	private synchronized void createGame(WebSocketSession session, Message message) throws IOException {
		
		Player player = new Player("White");
		player.initialize(Color.WHITE, session);
		
		MsgCode resultCode = gameService.createGame(message.gameID, player);
		Message response = new Message(resultCode, message.gameID, (String[])null);
		
		if(resultCode == MsgCode.GAME_EXISTS)
			notifySessionUser(message.gameID, session, resultCode);
		else
			notifyBoth(response);
	}
	
	private synchronized void joinGame(WebSocketSession session, Message message) throws IOException {
		
		Player player = new Player("Black");
		player.initialize(Color.BLACK, session);
		
		GameID gameID = message.gameID;
		
		MsgCode resultCode = gameService.joinGame(gameID, player);
		
		if(resultCode != MsgCode.GAME_STARTED)
			notifySessionUser(gameID, session, resultCode);
		else
			sendInitializationMessages(gameID, resultCode);
	}
	
	public void notifySessionUser(GameID gameID, WebSocketSession session, MsgCode resultCode) {
		
		Player ghostPlayer = new Player(null);
		ghostPlayer.setWsSession(session);
		
		notifyOneNoArgsCode(gameID, ghostPlayer, resultCode);
	}
	
	private synchronized void clickedField(WebSocketSession session, Message message) {
		
		GameID gameID = message.gameID;
		Position position = Position.parse(message.ARGS[0]);
	
		MoveResult result = gameService.move(gameID, position, session);
		dispatchMoveResult(gameID, result);
	}
	
	private synchronized void invert(WebSocketSession session, Message message) {
		
		GameID gameID = message.gameID;
		if(gameService.invert(gameID, session)) {
			notifyBothNoArgsCode(gameID, MsgCode.INVERTED);
			sendWhoseMove(gameID);
		}
		else
			notifyBothNoArgsCode(gameID, MsgCode.INVERT_REQUEST);
	}
	
	private void sendInitializationMessages(GameID gameID, MsgCode resultCode) {

		sendGameStarted(gameID, resultCode);
		sendCheckersToAdd(gameID);
		sendPositionsToRemoveLater(gameID);
		sendPositionToSelect(gameID);
		sendWhoseMove(gameID);
	}
	
	private void sendGameStarted(GameID gameID, MsgCode resultCode) {
		
		Player[] players = gameService.getPlayers(gameID);
		
		for(Player player : players) {
			
			String color = player.getColor() == Color.BLACK ? "b" : "w";
			Message message = new Message(resultCode, gameID, color);
			notifyPlayer(player, message);
		}
	}

	private void dispatchMoveResult(GameID gameID, MoveResult result) {
		
		if(result == null)
			return;
		
		switch(result) {
		case MOVE_INITIALIZED:
			moveInitialized(gameID);
			break;
		case MOVE_IN_PROGRESS:
			moveInProgress(gameID);
			break;
		case MOVE_COMPLETED:
			moveCompleted(gameID);
			break;
		case MOVE_REJECTED:
			moveRejected(gameID);
			break;
		case GAME_OVER:
			moveCompleted(gameID);
			gameOver(gameID);
			break;
		default:
			break;
		}
	}
	
	private void moveInitialized(GameID gameID) {
		
		sendPositionToSelect(gameID);
		sendWhoseMove(gameID);
	}
	
	private void moveInProgress(GameID gameID) {

		sendCheckersToAdd(gameID);
		sendPositionToSelect(gameID);
		sendPositionsToRemove(gameID);
		sendPositionsToRemoveLater(gameID);
		sendWhoseMove(gameID);
	}
	
	private void moveCompleted(GameID gameID) {

		sendCheckersToAdd(gameID);
		sendPositionsToRemove(gameID);
		sendWhoseMove(gameID);
	}
	
	private void moveRejected(GameID gameID) {
		
		notifyBothNoArgsCode(gameID, MsgCode.INVALID_MOVE);
	}
	
	private void gameOver(GameID gameID){
		
		Color winnerColor = whoWon(gameID);
		
		if(winnerColor == null)
			notifyBothNoArgsCode(gameID, MsgCode.DRAW);
		else
			gameWonByPlayer(gameID, winnerColor);
		
	}

	private Color whoWon(GameID gameID) {
		MsgCode whoWon = gameService.whoWon(gameID);
		Color winnerColor = whoWon == MsgCode.DRAW ? null : (whoWon == MsgCode.BLACK_WON ? Color.BLACK : Color.WHITE);
		return winnerColor;
	}

	private void gameWonByPlayer(GameID gameID, Color winnerColor) {
		
		for(Player player : gameService.getPlayers(gameID)) {
			if(player.getColor() == winnerColor)
				notifyOneNoArgsCode(gameID, player, MsgCode.YOU_WON);
			else
				notifyOneNoArgsCode(gameID, player, MsgCode.YOU_LOST);
		}
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
	
	private void sendPositionsToRemove(GameID gameID) {
		
		String[] positions = gameService.getCheckersToRemove(gameID);
		Message message = new Message(MsgCode.CHECKER_OFF_FIELD, gameID, positions);
		
		notifyBoth(message);
	}
	
	private void sendPositionsToRemoveLater(GameID gameID) {
		
		String[] positions = gameService.getCheckersToRemoveLater(gameID);
		if(positions == null || positions.length == 0 || positions[0].trim().length() == 0)
			return;
		
		Message message = new Message(MsgCode.CHECKER_TO_KILL, gameID, positions);
		
		notifyBoth(message);
	}

	
	private void sendPositionToSelect(GameID gameID) {
		
		String position = gameService.getSelectedPosition(gameID);
		if(position == null || position.trim().length() == 0)
			return;
		
		Message message = new Message(MsgCode.CHECKER_SELECTED, gameID, position);
		
		notifyBoth(message);
	}
	
	private void sendWhoseMove(GameID gameID) {
		
		Player playerActive = gameService.whoseMove(gameID);
		notifyOneNoArgsCode(gameID, playerActive, MsgCode.YOUR_MOVE);
		
		Player playerWaiting = gameService.whoIsWaiting(gameID);
		notifyOneNoArgsCode(gameID, playerWaiting, MsgCode.DEACTIVATE);
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
