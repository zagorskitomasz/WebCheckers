package com.webcheckers.api.domain.moves;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Field;

public class Path {

	private Deque<Field> path;
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
	
	public Path(Field starter) {
		
		this();
		path.add(starter);
		promoted = starter.getChecker().isPromoted();
		playerColor = starter.getChecker().COLOR;
	}

	public Deque<Field> getPath() {
		return path;
	}

	public void addStep(Field step) {
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

	public Field getLastField() {
		return path.peekLast();
	}
	
	public boolean isEmpty(Field field) {
		
		return !field.hasChecker() || wouldBeKilled(field.getChecker());
	}
	
	public boolean canMoveDeffensively(int yDir) {
		
		return getStrength() == 0 && (isProperDirection(yDir) || promoted);
	}
	
	private boolean isProperDirection(int yDir) {
		
		return (yDir == 1 && playerColor == Color.WHITE) || (yDir == -1 && playerColor == Color.BLACK);
	}

	public boolean startsFrom(Position position) {
		
		Field firstField = path.peekFirst();
		
		return firstField != null && firstField.POSITION.X == position.X && firstField.POSITION.Y == position.Y;
	}

	public boolean leadsFromTo(Position from, Position to) {
		
		if(!startsFrom(from))
			return false;
		
		Path tempPath = this.clone();
		tempPath.trimFirst();
		
		return tempPath.startsFrom(to);
	}
	
	private void trimFirst() {
		
		path.pollFirst();
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(Field field : path)
			stringBuilder.append(field.POSITION.X + " " + field.POSITION.Y + " -> ");
		
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
		
		Iterator<Field> thisIterator = path.iterator();
		Iterator<Field> otherIterator = otherPath.path.iterator();
		
		while(thisIterator.hasNext() && otherIterator.hasNext())
			if(!thisIterator.next().equals(otherIterator.next()))
				return false;
		
		return true;
	}
}
