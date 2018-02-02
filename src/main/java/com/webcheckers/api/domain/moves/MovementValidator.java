package com.webcheckers.api.domain.moves;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.game.Board;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Field;
import com.webcheckers.api.domain.game.Player;

public class MovementValidator {

	private Board board;
	private Color playerColor;
	private List<Path> possibilities;
	private List<Path> newPossibilities;

	public MovementValidator(Board board) {
		this.board = board;
	}

	public void clean() {
		playerColor = null;
		possibilities = null;
	}
	
	public List<Path> getPossibilities(){
		return possibilities;
	}

	public boolean hasAnyPossibility(Player player) {
		
		if(playerColor != player.getColor()) {
			clean();
			playerColor = player.getColor();
		}
		if (possibilities == null)
			createPossibilities();
		
		return possibilities.size() > 0;
	}
	
	public boolean canIStartWith(Player player, Position position) {

		if(playerColor != player.getColor()) {
			clean();
			playerColor = player.getColor();
		}
		Field field = board.getField(position);

		if (!isPlayersChecker(field))
			return false;

		if (possibilities == null)
			createPossibilities();
		
		return isAnyPathStartWith(position);
	}
	
	public boolean canIMoveThere(Checker mover, Position from, Position to) {
		
		if(!isPlayersChecker(mover.getField()))
			return false;
		
		return isAnyPathFromTo(from, to);
	}

	private boolean isPlayersChecker(Field field) {

		return field.hasChecker() && playerColor == field.getChecker().COLOR;
	}

	private void createPossibilities() {
		possibilities = new CopyOnWriteArrayList<>();

		initializePaths();
		
		boolean keepRunning = true;
		
		while(keepRunning)
			keepRunning = examineNextPathsLayer();
	}

	private void initializePaths() {

		List<Checker> starters = board.getAllPlayerCheckers(playerColor);
		for (Checker checker : starters) {
			Path path = new Path(checker.getField());
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
		
		Field lastField = originalPath.getLastField();
		Field nextField = lastField;
		Checker toKill = null;
		boolean blocked = false;
		boolean promoted = originalPath.isPromoted();
		
		do {
			nextField = getNextPosition(nextField, xDir, yDir);
			
			if(nextField == null)
				break;
			
			if(originalPath.isEmpty(nextField)) {
				if(toKill != null) 
					addNewPathBranch(originalPath, nextField, toKill);
				else if(originalPath.canMoveDeffensively(yDir)) 
					addNewPathBranch(originalPath, nextField, null);
				blocked = !promoted;
			}
			
			if(nextField.hasChecker()) {
				if(isSameColor(nextField))
					blocked = true;
				else if(toKill == null  && !originalPath.wouldBeKilled(nextField.getChecker()))
					toKill = nextField.getChecker();
				else
					blocked = true;
			}
				
		}
		while(nextField != null && !blocked);
	}

	private boolean madeDefensiveMove(Path originalPath) {
		
		return originalPath.getLength() > 1 && originalPath.getStrength() == 0;
	}

	private boolean isSameColor(Field nextField) {
		
		return nextField.getChecker().COLOR == playerColor;
	}

	private void addNewPathBranch(Path originalPath, Field nextField, Checker checker) {
		
		Path nextPath = originalPath.clone();
		originalPath.setToDelete(true);
		nextPath.addStep(nextField);
		
		if(checker != null)
			nextPath.addKilled(checker);
		
		newPossibilities.add(nextPath);
	}
	
	private Field getNextPosition(Field lastField, int xDir, int yDir) {
		
		int nextX = lastField.POSITION.X + xDir;
		int nextY = lastField.POSITION.Y + yDir;
		
		if(nextX < 0 || nextX > 9 || nextY < 0 || nextY > 9)
			return null;
		
		return board.getField(new Position(nextX, nextY));
	}

	private boolean isAnyPathStartWith(Position position) {
		
		for(Path path : possibilities) {
			if(path.startsFrom(position))
				return true;
		}
		return false;
	}

	private boolean isAnyPathFromTo(Position from, Position to){
		
		for(Path path : possibilities) {
			if(path.leadsFromTo(from, to))
				return true;
		}
		return false;
	}

	public Path isMoveCompleted(Movement currentMovement) {
		
		for(Path path : possibilities) {
			if(path.startsFrom(currentMovement.getFrom().POSITION) && path.endsIn(currentMovement.getTo().POSITION))
				return path;
		}
		return null;
	}

	public List<Checker> updatePossibilities(Movement currentMovement) {
		
		List<Checker> toBeKilled = null;
		
		for(Path path : possibilities) {
			if(!path.update(currentMovement))
				possibilities.remove(path);
			else
				toBeKilled = path.getKilled();
		}
		return toBeKilled;
	}

	public List<Checker> getKilledOne(Movement currentMovement) {
		
		for(Path path : possibilities) {
			return path.getKilledOne();
		}
		return null;
	}
}
