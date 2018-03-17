package com.webcheckers.api.domain.game;

import java.util.LinkedList;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

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
	private InvertRequest invertRequest;
	
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
			Integer freeIndex = getFreePlayerIndex();
			if(freeIndex == null)
				return false;
		
			Player waitingPlayer = players[(freeIndex + 1) % 2];
			
			if(waitingPlayer != null)
				updatePlayer(player, waitingPlayer.getColor() == Color.WHITE ? Color.BLACK : Color.WHITE);
			else
				updatePlayer(player, Color.WHITE);
		
			players[freeIndex] = player;
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}

	private void updatePlayer(Player player, Color color) {
		
		player.setColor(color);
		player.setName(color == Color.BLACK ? "Black" : "White");
	}
	
	private Integer getFreePlayerIndex() {
		
		for(int i = 0; i < players.length; i++) {
			if(players[i] == null)
				return i;
		}
		return null;
	}
	
	@Override
	public boolean bothPlayersJoined() {
		
		return getFreePlayerIndex() == null;
	}

	@Override
	public Player start() {
		
		if(players == null || players.length != 2 || players[0] == null || players[1] == null)
			throw new GameNotInitializedException();
		
		if(gameNotInitialized())
			initializeGame();
		prepareInitialCheckers();
		
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
		
		invertRequest = new InvertRequest();
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
	public Player whoIsWaiting() {
		
		assertInitialized();
		
		return getPlayer((active + 1) % 2);
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
	public MoveResult move(Position position, WebSocketSession session) {
		
		assertInitialized();
		
		if(!isProperPlayerMoving(session))
			return null;
		
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
	
	private boolean isProperPlayerMoving(WebSocketSession session) {
		
		WebSocketSession properPlayersSession = whoseMove().getWsSession();
		return properPlayersSession.equals(session);
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
		joinRemoveList();
		
		updateDefensiveCounter();
		currentMovement = null;
		
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
		selectedPosition = currentMovement.getMover().getField().POSITION;
		
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
				players[0] == null ||
				players[1] == null ||
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
		
		if(checkersToRemove.size() == 1 && currentMovement.getMover().isPromoted())
			defensiveCounter[active]++;
		else
			resetDefensiveCounter();
	}
	
	private void splitRemoveList() {
		
		String enemyColorLetter = extractEnemyColorLetter();
		
		addEnemyCheckersToLaterList(enemyColorLetter);
		removeEnemyCheckersFromRemoveList(enemyColorLetter);
	}
	
	private void joinRemoveList() {
		
		checkersToRemove.addAll(checkersToRemoveLater);
	}

	private String extractEnemyColorLetter() {
		
		return players[active].getColor() == Color.BLACK ? "w" : "b";
	}

	private void addEnemyCheckersToLaterList(String enemyColorLetter) {
		
		checkersToRemove.forEach(position -> {
			if(positionHasColorChecker(enemyColorLetter, position))
				checkersToRemoveLater.add(position);
		});
	}

	private void removeEnemyCheckersFromRemoveList(String enemyColorLetter) {
		
		checkersToRemove.removeIf(position -> positionHasColorChecker(enemyColorLetter, position));
	}

	private boolean positionHasColorChecker(String enemyColorLetter, Position position) {
		
		return board.hasChecker(position) && board.getChecker(position).toString().toLowerCase().contains(enemyColorLetter);
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
	
	@Override
	public boolean containsSession(WebSocketSession session) {
		
		for(Player player : players) {
			if(player != null && session.equals(player.getWsSession()))
				return true;
		}
		return false;
	}
	
	private Integer getPlayerBySession(WebSocketSession session) {
		
		for(int i = 0; i < players.length; i++) {
			if(players[i] != null && session.equals(players[i].getWsSession()))
				return i;
		}
		return null;
	}
	
	@Override
	public void removePlayerBySession(WebSocketSession session) {
		
		Integer index = getPlayerBySession(session);
		if(index != null)
			players[index] = null;
	}

	@Override
	public boolean invert(WebSocketSession session) {
		
		Integer index = getPlayerBySession(session);
		if(index == null)
			return false;
		
		invertRequest.addPlayer(players[index].getColor());
		
		if(invertRequest.isCompleted()) {
			invertGame();
			return true;
		}
		return false;
	}

	private void invertGame() {
		
		for(Player player : players)
			player.invert();
		
		active = (active + 1) % 2;
		
		int counter0 = defensiveCounter[0];
		int counter1 = defensiveCounter[1];
		
		defensiveCounter[0] = counter1;
		defensiveCounter[1] = counter0;
		
		invertRequest.reset();
	}
}
