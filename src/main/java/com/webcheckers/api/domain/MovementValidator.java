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
		clearPaths();
		checkNextStep();
		clearPaths();
	}

	private void initializePaths() {

		List<Checker> starters = board.getAllPlayerCheckers(playerColor);
		for (Checker checker : starters) {
			Path path = new Path(checker.getPosition());
			possibilities.add(path);
		}
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
			examineLeft(possibility);
			examineRight(possibility);
		}
		appendNewPossibilities();
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
		if(originalPath.isPromoted())
			examinePromotedPath(originalPath, direction);
		else if(playerColor == Color.WHITE)
			examinePath(originalPath, direction, 1);
		else
			examinePath(originalPath, direction, -1);
	}
	
	private void examinePromotedPath(Path originalPath, int xDir) {
		Position lastPosition = originalPath.getLastPosition();
		//TODO
	}
	
	private void examinePath(Path originalPath, int xDir, int yDir) {
		
		if(originalPath.getLength() > 1 && originalPath.getStrength() == 0)
			return;
		
		Position lastPosition = originalPath.getLastPosition();
		
		int nextX = lastPosition.X + xDir;
		int nextY = lastPosition.Y + yDir;
		
		if(nextX < 0 || nextX > 9 || nextY < 0 || nextY > 9)
			return;
		
		Position nextPosition = board.getPosition(nextX, nextY);
		if(originalPath.getStrength() == 0 && (nextPosition.getChecker() == null || originalPath.wouldBeKilled(nextPosition.getChecker()))){
			
			Path nextPath = originalPath.clone();
			originalPath.setToDelete(true);
			nextPath.addStep(nextPosition);
			newPossibilities.add(nextPath);
			return;
		}
		else if (nextPosition.getChecker().COLOR != playerColor){
			
			nextX = nextPosition.X + xDir;
			nextY = nextPosition.Y + yDir;
			
			if(nextX < 0 || nextX > 9 || nextY < 0 || nextY > 9)
				return;
			
			nextPosition = board.getPosition(nextX, nextY);
			if(nextPosition.getChecker() == null || originalPath.wouldBeKilled(nextPosition.getChecker())){
				
				Path nextPath = originalPath.clone();
				originalPath.setToDelete(true);
				nextPath.addStep(nextPosition);
				nextPath.addKilled(board.getPosition(lastPosition.X + xDir, lastPosition.Y + yDir).getChecker());
				newPossibilities.add(nextPath);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		Board board = new Board();
		Player player = new Player("Tomek");
		player.initialize(Color.WHITE, null);
		MovementValidator validator = new MovementValidator(board);
		validator.canIStartWith(player, 0, 0);
		System.out.println(board);
		validator.possibilities.forEach(System.out::println);
	}
}
