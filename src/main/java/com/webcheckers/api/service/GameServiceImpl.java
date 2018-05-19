package com.webcheckers.api.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.game.PolishChekersGame;
import com.webcheckers.api.domain.moves.Position;
import com.webcheckers.api.persistence.GamePersister;

@Service
public class GameServiceImpl implements GameService {

	private Map<GameID, Game> games;
	
	private GameDestroyer gameDestroyer;
	
	@Autowired
	private GamePersister gamePersister;
	
	@Autowired
	public GameServiceImpl(GameDestroyer gameDestroyer) {
		
		this.gameDestroyer = gameDestroyer;
		
		games = new ConcurrentHashMap<>();
		runDestroyer();
	}
	
	private void runDestroyer() {
		
		gameDestroyer.initialize(games);
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	    executorService.scheduleAtFixedRate(gameDestroyer, 0, GameDestroyerImpl.RUN_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public MsgCode createGame(GameID gameID, Player player) {
		
		if(games.containsKey(gameID) || gamePersister.gameExists(gameID.NAME))
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

		try {
			Game game = games.get(gameID);
		
			if(game == null) {
				return checkPersistedGame(gameID, player);
			}
		
			if(samePlayer(gameID, player))
				return MsgCode.ERROR;
		
			if(!game.join(player))
				return MsgCode.GAME_FULL;
		
			if(!game.bothPlayersJoined())
				return MsgCode.GAME_CREATED;
		
			startGame(gameID, game);
			return MsgCode.GAME_STARTED;
		}
		catch(Exception ex) {
			return MsgCode.ERROR;
		}
	}
	
	private MsgCode checkPersistedGame(GameID gameID, Player player) {
		
		if(gamePersister.gameExists(gameID.NAME)) {
			
			Game game = new PolishChekersGame();
			
			if(game.create(player)) {
				games.put(gameID, game);
				return MsgCode.GAME_CREATED;
			}
			else
				return MsgCode.ERROR;
		}
		else
			return MsgCode.GAME_NOT_EXIST;
	}

	private void startGame(GameID gameID, Game game) {
		
		game.start();
		gamePersister.syncWithDB(gameID, game);
		game.refreshCheckersList();
	}
	
	private boolean samePlayer(GameID gameID, Player player) {
		
		Game game = games.get(gameID);
		
		return game.containsSession(player.getWsSession());
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
	public Player whoIsWaiting(GameID gameID) {
		
		Game game = games.get(gameID);
		
		try {
			return game.whoIsWaiting();
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

	@Override	
	public String[] getCheckersToRemoveLater(GameID gameID) {
		
		Game game = games.get(gameID);
		try {
			@SuppressWarnings("unchecked")
			List<Position> positions = (List<Position>)game.removeFromBoardLater();
			
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
	public MoveResult move(GameID gameID, Position position, WebSocketSession session) {
		
		Game game = games.get(gameID);
		try {
			MoveResult result = game.move(position, session);
			switch(result) {
			case MOVE_COMPLETED:
				gamePersister.saveState(gameID, game);
				break;
			case GAME_OVER:
				gamePersister.archivise(gameID, game);
				break;
			default:
				break;
			}
			return result;
		}
		catch(Exception ex) {
			ex.printStackTrace();
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
	
	@Override
	public Player playerDisconnected(WebSocketSession session) {
		
		Game game = getGameBySession(session);
		
		if(game == null)
			return null;
		
		Player[] players = updatePlayers(session, game);
		
		for(Player player : players) {
			if(player != null)
				return player;
		}
		return null;
	}
	
	private Game getGameBySession(WebSocketSession session) {
		
		for(Game game : games.values()) {
			if(game.containsSession(session))
				return game;
		}
		return null;
	}

	private Player[] updatePlayers(WebSocketSession session, Game game) {
		
		game.removePlayerBySession(session);
		Player[] players = game.getPlayers();
		
		return players;
	}

	@Override
	public boolean invert(GameID gameID, WebSocketSession session) {
		
		Game game = games.get(gameID);
		try {
			return game.invert(session);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
