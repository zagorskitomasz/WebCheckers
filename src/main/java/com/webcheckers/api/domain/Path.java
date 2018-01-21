package com.webcheckers.api.domain;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path {

	private List<Position> path;
	private List<Checker> killed;
	private boolean longest;
	private boolean promoted;
	private boolean toDelete;
	private Color playerColor;
	
	public Path() {
		
		path = new LinkedList<>();
		killed = new LinkedList<>();
		longest = true;
	}
	
	public Path(Position starter) {
		
		this();
		path.add(starter);
		promoted = starter.getChecker().isPromoted();
		playerColor = starter.getChecker().COLOR;
	}

	public List<Position> getPath() {
		return path;
	}

	public void addStep(Position step) {
		path.add(step);
	}
	
	public void setLongest(boolean longest) {
		this.longest = longest;
	}
	
	public boolean isLongest() {
		return longest;
	}
	
	public void addKilled(Checker checker) {
		killed.add(checker);
	}
	
	public boolean wouldBeKilled(Checker checker) {
		return killed.contains(checker);
	}
	
	public void promote() {
		promoted = true;
	}
	
	public boolean isPromoted() {
		return promoted;
	}
	
	public int getStrength() {
		return killed.size();
	}
	
	public int getLength() {
		return path.size();
	}
	
	public boolean isToDelete() {
		return toDelete;
	}

	public void setToDelete(boolean toDelete) {
		this.toDelete = toDelete;
	}

	@SuppressWarnings("unchecked")
	public Position getLastPosition() {
		Position lastPosition = ((Deque<Position>)path).peekLast();
		return lastPosition;
	}
	
	public boolean isEmpty(Position position) {
		
		return !position.hasChecker() || wouldBeKilled(position.getChecker());
	}
	
	public boolean canMoveDeffensively(int yDir) {
		
		return getStrength() == 0 && (isProperDirection(yDir) || promoted);
	}
	
	private boolean isProperDirection(int yDir) {
		
		return (yDir == 1 && playerColor == Color.WHITE) || (yDir == -1 && playerColor == Color.BLACK);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(Position position : path)
			stringBuilder.append(position.X + " " + position.Y + " -> ");
		
		stringBuilder.append("killed: " + getStrength());
		
		return stringBuilder.toString();
	}
	
	@Override
	public Path clone() {
		Path clonedPath = new Path();
		clonedPath.path.addAll(this.path);
		clonedPath.killed.addAll(this.killed);
		clonedPath.promoted = this.promoted;
		
		return clonedPath;
	}
	
	@Override
	public boolean equals(Object other) {
		
		Path otherPath = (Path)other;
		
		if(getLength() == 6 && otherPath.getLength() == 6)
			this.getClass();
		
		if(getLength() != otherPath.getLength() || getStrength() != otherPath.getStrength())
			return false;
		
		Iterator<Position> thisIterator = path.iterator();
		Iterator<Position> otherIterator = otherPath.path.iterator();
		
		while(thisIterator.hasNext() && otherIterator.hasNext())
			if(!thisIterator.next().equals(otherIterator.next()))
				return false;
		
		return true;
	}
}
