package com.webcheckers.api.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.game.PolishChekersGame;
import com.webcheckers.api.domain.moves.Position;

@Service
public class GameServiceImpl implements GameService {

	private Map<GameID, Game> games;
	
	public GameServiceImpl() {
		
		games = new ConcurrentHashMap<>();
		runDestroyer();
	}
	
	private void runDestroyer() {
		
		Runnable destroyer = new GameDestroyer(games);
		
		Thread destroyerThread = new Thread(destroyer);
		destroyerThread.start();
	}
	
	@Override
	public MsgCode createGame(GameID gameID, Player player) {
		
		if(games.containsKey(gameID))
			return MsgCode.GAME_EXISTS;
		
		for(GameID ID : games.keySet()) {
			
			if(ID.NAME.equals(gameID.NAME))
				return MsgCode.GAME_EXISTS;
		}
		
		Game game = new PolishChekersGame();
		
		if(game.create(player)) {
			games.put(gameID, game);
			return MsgCode.GAME_CREATED;
		}
		return MsgCode.ERROR;
	}

	@Override
	public MsgCode joinGame(GameID gameID, Player player) {
		
		Game game = games.get(gameID);
		
		if(game == null)
			return MsgCode.ERROR;
		
		if(!game.join(player))
			return MsgCode.GAME_FULL;
		
		try {
			game.start();
			return MsgCode.GAME_STARTED;
		}
		catch(Exception ex) {
			return MsgCode.ERROR;
		}
	}

	@Override
	public Player whoseMove(GameID gameID) {
		
		Game game = games.get(gameID);
		
		try {
			return game.whoseMove();
		}
		catch(Exception ex) {
			return null;
		}
	}

	@Override
	public String[] getCheckersToAdd(GameID gameID) {
		
		Game game = games.get(gameID);
		try {
			@SuppressWarnings("unchecked")
			List<Checker> checkers = (List<Checker>)game.addToBoard();
			
			return encryptCheckers(checkers);
		}
		catch(Exception ex) {
			return new String[0];
		}
	}

	private String[] encryptCheckers(List<Checker> checkers) {
		StringBuilder builder = new StringBuilder();
		
		for(Checker checker : checkers) {
			appendChecker(builder, checker);
		}
		String longString = builder.toString();
		return longString.split(" ");
	}

	private void appendChecker(StringBuilder builder, Checker checker) {
		
		if(builder.length() > 0)
			builder.append(" ");
		
		builder.append(checker.getField().POSITION + "$" + checker);
	}

	@Override
	public String[] getCheckersToRemove(GameID gameID) {
		
		Game game = games.get(gameID);
		try {
			@SuppressWarnings("unchecked")
			List<Position> positions = (List<Position>)game.removeFromBoard();
			
			return encryptPositions(positions);
		}
		catch(Exception ex) {
			return new String[0];
		}
	}

	private String[] encryptPositions(List<Position> positions) {
		StringBuilder builder = new StringBuilder();
		
		for(Position position : positions) {
			appendPosition(builder, position);
		}
		String longString = builder.toString();
		return longString.split(" ");
	}

	private void appendPosition(StringBuilder builder, Position position) {
		
		if(builder.length() > 0)
			builder.append(" ");
		
		builder.append(position);
	}

	@Override
	public String getSelectedPosition(GameID gameID) {
		
		Game game = games.get(gameID);
		try {
			return game.getSelectedPosition().toString();
		}
		catch(Exception ex) {
			return "";
		}
	}

	@Override
	public MoveResult move(GameID gameID, Position position) {
		
		Game game = games.get(gameID);
		try {
			return game.move(position);
		}
		catch(Exception ex) {
			return MoveResult.MOVE_REJECTED;
		}
	}

	@Override
	public MsgCode whoWon(GameID gameID) {
		
		Game game = games.get(gameID);
		
		try {
			Player player = game.whoWon();
			
			if(player == null)
				return MsgCode.DRAW;
			
			if(player.getColor() == Color.BLACK)
				return MsgCode.BLACK_WON;
			
			return MsgCode.WHITE_WON;
		}
		catch(Exception ex) {
			return null;
		}
	}

	@Override
	public void destroyGame(GameID gameID) {
		
		games.remove(gameID);
	}

	@Override
	public Player[] getPlayers(GameID gameID) {
		
		try {
			Game game = games.get(gameID);
			return game.getPlayers();
		}
		catch(Exception ex) {
			return null;
		}
	}

}
