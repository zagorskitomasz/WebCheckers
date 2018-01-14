package com.webcheckers.api.domain;

import java.util.LinkedList;
import java.util.List;

public class Path {

	private List<Position> path;
	private List<Checker> killed;
	private boolean longest;
	private boolean promoted;
	
	public Path() {
		
		path = new LinkedList<>();
		killed = new LinkedList<>();
		longest = true;
	}
	
	public Path(Position starter) {
		
		this();
		path.add(starter);
		promoted = starter.getChecker().isPromoted();
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
	
	@Override
	public Path clone() {
		Path clonedPath = new Path();
		clonedPath.path.addAll(this.path);
		clonedPath.killed.addAll(this.killed);
		
		return clonedPath;
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
}
