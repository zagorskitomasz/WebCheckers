package com.webcheckers.api.domain.game;

import java.util.LinkedList;
import java.util.List;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.moves.MovementValidator;
import com.webcheckers.api.domain.moves.Position;
import com.webcheckers.api.exceptions.GameNotInitializedException;

public class PolishChekersGame implements Game {

	private Board board;
	
	private Player[] players;
	private Integer winner;
	private Integer active;
	
	private MovementValidator validator;
	private Integer[] defensiveCounter;
	
	private List<Checker> checkersToUpdate;
	private List<Checker> checkersToRemove;
	
	private String message;
	
	@Override
	public boolean create(Player player) {
		
		players = new Player[] {player, null};
		
		return true;
	}

	@Override
	public boolean join(Player player) {
		
		try {
			if(players[0].getColor() == player.getColor()) {
				
				message = "Players must have different colors!";
				return false;
			}
			players[1] = player;
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}

	@Override
	public Player start() {
		
		if(players == null || players.length != 2)
			throw new GameNotInitializedException();
		
		initializeGame();
		
		message = "Game initialized. Default checkers are ready.";
		
		return whoseMove();
	}

	private void initializeGame() {
		initializeObjects();
		initializeIntegers();
		
		assertInitialized();
		
		prepareInitialCheckers();
	}

	private void initializeObjects() {
		
		board = new Board();
		validator = new MovementValidator(board);
		
		checkersToRemove = new LinkedList<Checker>();
		checkersToUpdate = new LinkedList<Checker>();
	}

	private void initializeIntegers() {
		
		winner = -1;
		active = getPlayerIndex(Color.WHITE);
		resetDefensiveCounter();
	}
	
	private void resetDefensiveCounter() {
	
		defensiveCounter = new Integer[] {0, 0};
	}
	
	private void prepareInitialCheckers() {
		
		checkersToUpdate = board.getAllCheckers();
	}

	@Override
	public Player whoseMove() {
		
		assertInitialized();
		
		return getPlayer(active);
	}

	@Override
	public Player whoWon() {
		
		assertInitialized();
		
		return getPlayer(winner);
	}

	@Override
	public MoveResult move(Position position) {
		
		assertInitialized();
		
		//TODO
		
		return null;
	}
	
	public List<?> removeFromBoard(){
		
		assertInitialized();
		
		return checkersToRemove;
	}
	 
	public List<?> addToBoard(){
		
		assertInitialized();
		
		return checkersToUpdate;
	}
	
	public String getMessage() {
		
		return message;
	}

	private Player getPlayer(Integer index) {
		
		return players[index];
	}
	
	private Integer getPlayerIndex(Color color) {
		
		for(int i = 0; i < players.length; i++) {
			if(players[i].getColor() == color)
				return i;
		}
		throw new GameNotInitializedException();
	}
	
	private void assertInitialized() {
		
		if(gameNotInitialized())
			throw new GameNotInitializedException();
	}
	
	private boolean gameNotInitialized() {
		
		return players == null || 
				defensiveCounter == null ||
				players.length != 2 ||
				defensiveCounter.length != 2 ||
				board == null ||
				validator == null ||
				checkersToRemove == null ||
				checkersToUpdate == null;
	}
}
