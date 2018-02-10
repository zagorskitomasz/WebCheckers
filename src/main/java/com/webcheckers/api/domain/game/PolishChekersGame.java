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
import com.webcheckers.api.exceptions.GameNotOverException;

public class PolishChekersGame implements Game {

	private Board board;
	
	private Player[] players;
	private Integer winner;
	private Integer active;
	
	private Movement currentMovement;
	private MovementValidator validator;
	private Integer[] defensiveCounter;
	private boolean isGameOver = false;
	
	private List<Checker> checkersToUpdate;
	private List<Position> checkersToRemove;
	private List<Position> checkersToRemoveLater;
	private Position selectedPosition;
	
	private String message;
	
	@Override
	public boolean create(Player player) {
		
		players = new Player[] {player, null};
		
		return true;
	}

	@Override
	public boolean join(Player player) {
		
		try {
			if(players[0].getColor() == player.getColor() || players[1] != null) 
				return false;
			
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
		checkersToRemoveLater = new LinkedList<Position>();
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
		
		if(isGameOver) {
			if(winner >= 0)
				return getPlayer(winner);
			else
				return null;
		}
		else
			throw new GameNotOverException();
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
		selectedPosition = currentMovement.getMover().getField().POSITION;
		
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
		
		updateDefensiveCounter();
		
		active = (active + 1) % 2;
		
		checkGameOver();
		
		if(isGameOver)
			return MoveResult.GAME_OVER;
		else
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
		splitRemoveList();
		
		return MoveResult.MOVE_IN_PROGRESS;
	}

	private MoveResult rejectMove() {
		
		return MoveResult.MOVE_REJECTED;
	}

	@Override
	public List<?> removeFromBoard(){
		
		assertInitialized();
		
		return checkersToRemove;
	}

	@Override
	public List<?> addToBoard(){
		
		assertInitialized();
		
		return checkersToUpdate;
	}

	@Override
	public List<?> removeFromBoardLater(){
		
		assertInitialized();
		
		return checkersToRemoveLater;
	}

	@Override
	public Position getSelectedPosition() {
		
		assertInitialized();
		
		return selectedPosition;
	}

	@Override
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
		checkersToRemoveLater.clear();
		checkersToUpdate.clear();
	}
	
	private void updateDefensiveCounter() {
		
		if(checkersToRemove.size() == 1 || board.getChecker(checkersToRemove.get(0)).isPromoted())
			defensiveCounter[active]++;
		else
			resetDefensiveCounter();
	}
	
	private void splitRemoveList() {
		
		String activeColorLetter = extractActiveColorLetter();
		
		addEnemyCheckersToLaterList(activeColorLetter);
		removeEnemyCheckersFromRemoveList(activeColorLetter);
	}

	private String extractActiveColorLetter() {
		
		return players[active].getColor() == Color.BLACK ? "b" : "w";
	}

	private void addEnemyCheckersToLaterList(String activeColorLetter) {
		
		checkersToRemove.forEach(position -> {
			if(positionHasColorChecker(activeColorLetter, position))
				checkersToRemoveLater.add(position);
		});
	}

	private void removeEnemyCheckersFromRemoveList(String activeColorLetter) {
		
		checkersToRemove.removeIf(position -> positionHasColorChecker(activeColorLetter, position));
	}

	private boolean positionHasColorChecker(String activeColorLetter, Position position) {
		
		return board.getChecker(position).toString().toLowerCase().contains(activeColorLetter);
	}
	
	private void checkGameOver() {
		
		if(!validator.hasAnyPossibility(players[active])) {
			
			oneOfPlayersWon();
			return;
		}
		if(defensiveCounter[0] >= 15 && defensiveCounter[1] >= 15) {
			
			draw();
			return;
		}
	}

	private void draw() {
		winner = -1;
		isGameOver = true;
		message = "Draw!";
	}

	private void oneOfPlayersWon() {
		
		winner = (active + 1) % 2;
		isGameOver = true;
		message = players[winner].getName() + " won!";
	}

	@Override
	public Player[] getPlayers() {
		return players;
	}
}
