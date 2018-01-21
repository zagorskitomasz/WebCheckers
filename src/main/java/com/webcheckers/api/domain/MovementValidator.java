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

		playerColor = player.getColor();
		position = board.getPosition(x, y);

		if (!isPlayersChecker())
			return false;

		if (possibilities == null)
			createPossibilities();
		
		return false;
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
	
		boolean keepRunning = true;
		checkNextStep();
		
		if(newPossibilities.size() == 0)
			keepRunning = false;
		
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
		
		for(Path possibility : possibilities) {
			if(possibility.isPromoted())
				examinePromotedPath(possibility);
			else {
				examineLeft(possibility);
				examineRight(possibility);
			}
		}
	}

	private void appendNewPossibilities() {
		possibilities.removeIf(path -> path.isToDelete());
		possibilities.addAll(newPossibilities);
		newPossibilities = null;
	}
	
	private void examineLeft(Path possibility) {
		int leftDirection = -1;
		examinePath(possibility, leftDirection);
	}
	
	private void examineRight(Path possibility) {
		int rightDirection = 1;
		examinePath(possibility, rightDirection);
	}
	
	private void examinePath(Path originalPath, int direction) {
		
		if(playerColor == Color.WHITE)
			examinePath(originalPath, direction, 1);
		else
			examinePath(originalPath, direction, -1);
	}
	
	private void examinePromotedPath(Path originalPath) {
		
		if(madeDefensiveMove(originalPath))
			return;
		
		examinePromotedNW(originalPath);
		examinePromotedNE(originalPath);
		examinePromotedSW(originalPath);
		examinePromotedSE(originalPath);
	}
	
	private void examinePromotedNW(Path originalPath) {
		examinePromoted(originalPath, -1, -1);
	}
	
	private void examinePromotedNE(Path originalPath) {
		examinePromoted(originalPath, 1, -1);
	}
	
	private void examinePromotedSW(Path originalPath) {
		examinePromoted(originalPath, -1, 1);
	}
	
	private void examinePromotedSE(Path originalPath) {
		examinePromoted(originalPath, 1, 1);
	}
	
	private void examinePromoted(Path originalPath, int xDir, int yDir) {
		
		Position lastPosition = originalPath.getLastPosition();
		Position nextPosition = lastPosition;
		boolean killed = false;
		Checker toKill = null;
		boolean blocked = false;
		
		do {
			nextPosition = getNextPosition(nextPosition, xDir, yDir);
			
			if(nextPosition == null)
				break;
			
			if(!nextPosition.hasChecker() || originalPath.wouldBeKilled(nextPosition.getChecker())) {
				if(toKill != null) {
					addNewPathBranch(originalPath, nextPosition, toKill);
					killed = true;
				}
				else if(originalPath.getStrength() == 0)
					addNewPathBranch(originalPath, nextPosition, null);
			}
			
			if(nextPosition.hasChecker()) {
				if(nextPosition.getChecker().COLOR == playerColor)
					blocked = true;
				else if(!killed && !originalPath.wouldBeKilled(nextPosition.getChecker()))
					toKill = nextPosition.getChecker();
				else
					break;
			}
				
		}
		while(nextPosition != null && !blocked);
	}
	
	private void examinePath(Path originalPath, int xDir, int yDir) {
		
		if(madeDefensiveMove(originalPath))
			return;
		
		Position lastPosition = originalPath.getLastPosition();
		Position nextPosition = getNextPosition(lastPosition, xDir, yDir);
		
		if(nextPosition != null) {

			if(canMoveDefensive(originalPath, nextPosition))
				addNewPathBranch(originalPath, nextPosition, null);
			
			Position afterNextPosition = getNextPosition(nextPosition, xDir, yDir);

			if(afterNextPosition != null) {

				if (canMoveOffensive(originalPath, nextPosition, afterNextPosition))
					addNewPathBranch(originalPath, afterNextPosition, nextPosition.getChecker());
			}
		}

		nextPosition = getNextPosition(lastPosition, xDir, yDir * (-1));
		if(nextPosition != null) {
			
			Position afterNextPosition = getNextPosition(nextPosition, xDir, yDir * (-1));

			if(afterNextPosition != null) {

				if (canMoveOffensive(originalPath, nextPosition, afterNextPosition))
					addNewPathBranch(originalPath, afterNextPosition, nextPosition.getChecker());
			}
		}
	}

	private boolean madeDefensiveMove(Path originalPath) {
		
		return originalPath.getLength() > 1 && originalPath.getStrength() == 0;
	}

	private boolean canMoveDefensive(Path originalPath, Position nextPosition){
		
		return originalPath.getStrength() == 0 && 
				(nextPosition.getChecker() == null || 
				originalPath.wouldBeKilled(nextPosition.getChecker()));
	}

	private boolean canMoveOffensive(Path originalPath, Position nextPosition, Position afterNextPosition) {
		
		Checker toKill = nextPosition.getChecker();
		
		return toKill != null && 
				toKill.COLOR != playerColor && 
				!originalPath.wouldBeKilled(toKill) &&
				(!afterNextPosition.hasChecker() || 
						originalPath.wouldBeKilled(afterNextPosition.getChecker()));
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
	
	public static void main(String[] args) {
		Board board = new Board();
		Player player = new Player("Tomek");
		player.initialize(Color.WHITE, null);
		board.getChecker(1, 7).moveTo(board.getPosition(1, 3));
		board.getChecker(0, 8).moveTo(board.getPosition(3, 5));
		board.getChecker(5, 7).moveTo(board.getPosition(5, 5));
		board.getChecker(2, 8).moveTo(board.getPosition(5, 7));
		board.getChecker(4, 8).moveTo(board.getPosition(9, 5));
		board.getChecker(2, 2).promote();
		System.out.println(board);
		MovementValidator validator = new MovementValidator(board);
		validator.canIStartWith(player, 0, 0);
		validator.possibilities.forEach(System.out::println);
	}
}
