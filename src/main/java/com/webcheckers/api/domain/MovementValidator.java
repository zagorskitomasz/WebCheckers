package com.webcheckers.api.domain;

import java.util.LinkedList;
import java.util.List;

public class MovementValidator {

	private Board board;
	private Color playerColor;
	private Position position;
	private List<Path> possibilities;
	private List<Path> newPossibilities;

	public MovementValidator(Board board) {
		this.board = board;
	}

	public void clean() {
		playerColor = null;
		position = null;
		possibilities = null;
	}

	public boolean canIStartWith(Player player, int x, int y) {

		if(playerColor != player.getColor()) {
			clean();
			playerColor = player.getColor();
		}
		position = board.getPosition(x, y);

		if (!isPlayersChecker())
			return false;

		if (possibilities == null)
			createPossibilities();
		
		return isAnyPathStartWith(x, y);
	}

	private boolean isPlayersChecker() {

		return position.hasChecker() && playerColor == position.getChecker().COLOR;
	}

	private void createPossibilities() {
		possibilities = new LinkedList<>();

		initializePaths();
		
		boolean keepRunning = true;
		
		while(keepRunning)
			keepRunning = examineNextPathsLayer();
	}

	private void initializePaths() {

		List<Checker> starters = board.getAllPlayerCheckers(playerColor);
		for (Checker checker : starters) {
			Path path = new Path(checker.getPosition());
			possibilities.add(path);
		}
	}

	private boolean examineNextPathsLayer() {
	
		checkNextStep();
		
		boolean	keepRunning = newPossibilities.size() > 0;
		
		appendNewPossibilities();
		clearPaths();
		
		return keepRunning;
	}
	
	private void clearPaths() {
		removeNotStrongEnough();
		removeNotLongEnough();
	}

	private void removeNotStrongEnough() {
		
		int maxStrength = 0;
		for(Path path : possibilities)
			maxStrength = Math.max(maxStrength, path.getStrength());
		
		int finalStrength = maxStrength;
		possibilities.removeIf(path -> path.getStrength() < finalStrength);
	}
	
	private void removeNotLongEnough() {
		
		int maxLength = 0;
		for(Path path : possibilities)
			maxLength = Math.max(maxLength, path.getLength());
		
		int finalLength = maxLength;
		possibilities.removeIf(path -> path.getLength() < finalLength);
	}
	
	private void checkNextStep() {
		if(newPossibilities == null)
			newPossibilities = new LinkedList<>();
		
		for(Path possibility : possibilities) 
			examinePath(possibility);
	}

	private void appendNewPossibilities() {
		possibilities.removeIf(path -> path.isToDelete());
		possibilities.addAll(newPossibilities);
		newPossibilities = null;
	}
	
	private void examinePath(Path originalPath) {
		
		if(madeDefensiveMove(originalPath))
			return;
		
		examinePath(originalPath, -1, -1);
		examinePath(originalPath, -1, 1);
		examinePath(originalPath, 1, -1);
		examinePath(originalPath, 1, 1);
	}
	
	private void examinePath(Path originalPath, int xDir, int yDir) {
		
		Position lastPosition = originalPath.getLastPosition();
		Position nextPosition = lastPosition;
		Checker toKill = null;
		boolean blocked = false;
		boolean promoted = originalPath.isPromoted();
		
		do {
			nextPosition = getNextPosition(nextPosition, xDir, yDir);
			
			if(nextPosition == null)
				break;
			
			if(originalPath.isEmpty(nextPosition)) {
				if(toKill != null) 
					addNewPathBranch(originalPath, nextPosition, toKill);
				else if(originalPath.canMoveDeffensively(yDir)) 
					addNewPathBranch(originalPath, nextPosition, null);
				blocked = !promoted;
			}
			
			if(nextPosition.hasChecker()) {
				if(isSameColor(nextPosition))
					blocked = true;
				else if(toKill == null  && !originalPath.wouldBeKilled(nextPosition.getChecker()))
					toKill = nextPosition.getChecker();
				else
					blocked = true;
			}
				
		}
		while(nextPosition != null && !blocked);
	}

	private boolean madeDefensiveMove(Path originalPath) {
		
		return originalPath.getLength() > 1 && originalPath.getStrength() == 0;
	}

	private boolean isSameColor(Position nextPosition) {
		
		return nextPosition.getChecker().COLOR == playerColor;
	}

	private void addNewPathBranch(Path originalPath, Position nextPosition, Checker checker) {
		
		Path nextPath = originalPath.clone();
		originalPath.setToDelete(true);
		nextPath.addStep(nextPosition);
		
		if(checker != null)
			nextPath.addKilled(checker);
		
		newPossibilities.add(nextPath);
	}
	
	private Position getNextPosition(Position lastPosition, int xDir, int yDir) {
		
		int nextX = lastPosition.X + xDir;
		int nextY = lastPosition.Y + yDir;
		
		if(nextX < 0 || nextX > 9 || nextY < 0 || nextY > 9)
			return null;
		
		return board.getPosition(nextX, nextY);
	}

	private boolean isAnyPathStartWith(int x, int y) {
		
		for(Path path : possibilities) {
			if(path.startsFrom(x, y))
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		
		Board board = new Board();
		
		Player white = new Player("White");
		white.initialize(Color.WHITE, null);
		
		Player black = new Player("Black");
		black.initialize(Color.BLACK, null);
		
		for(int i = 0; i < 50; i++) {
			
			Checker checker = board.getRandomChecker();
			Position position = board.getRandomEmptyPosition();
			checker.moveTo(position);
			if(i % 10 == 0)
				checker.promote();
		}
		
		System.out.println(board);
		MovementValidator validator = new MovementValidator(board);
		
		int x = 0;
		int y = 0;
		System.out.println("Black start with " + x + ", " + y + ": " + validator.canIStartWith(black, x, y));
		
		if(validator.possibilities != null) {
			System.out.println("\nBlack possibilities:");
			validator.possibilities.forEach(System.out::println);
		}
		
		System.out.println("\nWhite start with " + x + ", " + y + ": " + validator.canIStartWith(white, x, y));
		
		if(validator.possibilities != null) {
			System.out.println("\nWhite possibilities:");
			validator.possibilities.forEach(System.out::println);
		}
	}
}
