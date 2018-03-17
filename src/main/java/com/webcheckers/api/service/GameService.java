package com.webcheckers.api.service;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.moves.Position;

public interface GameService {
	
	public MsgCode createGame(GameID gameID, Player player);
	
	public MsgCode joinGame(GameID gameID, Player player);
	
	public Player whoseMove(GameID gameID);
	
	public Player whoIsWaiting(GameID gameID);
	
	public String[] getCheckersToAdd(GameID gameID);
	
	public String[] getCheckersToRemove(GameID gameID);
	
	public String[] getCheckersToRemoveLater(GameID gameID);
	
	public String getSelectedPosition(GameID gameID);
	
	public MoveResult move(GameID gameID, Position position, WebSocketSession session);
	
	public MsgCode whoWon(GameID gameID);
	
	public void destroyGame(GameID gameID);

	public Player[] getPlayers(GameID gameID);
	
	public Player playerDisconnected(WebSocketSession session);

	public boolean invert(GameID gameID, WebSocketSession session);
}