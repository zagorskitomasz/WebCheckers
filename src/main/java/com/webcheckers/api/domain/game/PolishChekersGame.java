package com.webcheckers.api.domain.game;

import java.util.LinkedList;
import java.util.List;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.moves.Movement;
import com.webcheckers.api.domain.moves.MovementValidator;
import com.webcheckers.api.domain.moves.Path;
import com.webcheckers.api.domain.moves.Position;
import com.webcheckers.api.exceptions.GameNotInitializedException;

public class PolishChekersGame implements Game {

	private Board board;
	
	private Player[] players;
	private Integer winner;
	private Integer active;
	
	private Movement currentMovement;
	private MovementValidator validator;
	private Integer[] defensiveCounter;
	
	private List<Checker> checkersToUpdate;
	private List<Position> checkersToRemove;
	
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
		
		checkersToRemove = new LinkedList<Position>();
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
		
		if(currentMovement == null || currentMovement.getState() == MoveResult.MOVE_INITIALIZED){
			if(canStartFromHere(position))
				return setStartingPoint(position);
			else if(currentMovement == null)
				return rejectMove();
		}
		
		if(currentMovement.getState() == MoveResult.MOVE_INITIALIZED || 
				currentMovement.getState() == MoveResult.MOVE_IN_PROGRESS) {
			if(canGoThere(position))
				return setNextStep(position);
			else
				return rejectMove();
		}
		return rejectMove();
	}
	
	private boolean canStartFromHere(Position position) {
		
		return validator.canIStartWith(whoseMove(), position);
	}

	private MoveResult setStartingPoint(Position position) {
		
		clearLists();
		
		currentMovement = new Movement(whoseMove().getColor(), board.getField(position), null);
		currentMovement.setState(MoveResult.MOVE_INITIALIZED);
		
		return MoveResult.MOVE_INITIALIZED;
	}

	private boolean canGoThere(Position position) {
		
		return validator.canIMoveThere(currentMovement.getMover(), currentMovement.getFrom().POSITION, position);
	}

	private MoveResult setNextStep(Position position) {
		
		currentMovement.setTo(board.getField(position));
		
		Path finalPath = validator.isMoveCompleted(currentMovement);
		
		if(finalPath != null)
			return finalizeMovement(finalPath);
		else
			return continueMovement();
	}

	private MoveResult finalizeMovement(Path path) {
		
		updateMovement(path);
		
		updateBoard();
		updateLists();
		
		active = (active + 1) % 2;
		
		return MoveResult.MOVE_COMPLETED;
	}
	
	private void updateMovement(Path path) {
		
		currentMovement.setKilled(path.getKilled());
		currentMovement.checkPromote();
	}
	
	private void updateBoard() {

		moveChecker();
		board.killCheckers(currentMovement.getKilled());
	}

	private void moveChecker() {
		
		checkersToRemove.add(currentMovement.getMover().getField().POSITION);
		currentMovement.getMover().moveTo(currentMovement.getTo());
	}
	
	private void updateLists() {
		
		updateUpdateList();
		updateRemoveList();
	}
	
	private void updateUpdateList() {
		
		checkersToUpdate.add(currentMovement.getTo().getChecker());
	}

	private void updateRemoveList() {
		
		currentMovement.getKilled().forEach(checker -> checkersToRemove.add(checker.getField().POSITION));
	}

	private MoveResult continueMovement() {
		
		moveChecker();
		
		validator.updatePossibilities(currentMovement);
		currentMovement.setKilled(validator.getKilledOne(currentMovement));
		
		updateLists();
		
		return MoveResult.MOVE_IN_PROGRESS;
	}

	private MoveResult rejectMove() {
		
		return MoveResult.MOVE_REJECTED;
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
	
	private void clearLists() {
		
		checkersToRemove.clear();
		checkersToUpdate.clear();
	}
}
