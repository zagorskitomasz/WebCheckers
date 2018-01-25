package com.webcheckers.api.domain;

import java.util.HashSet;
import java.util.Set;

public class Movement {
	
	private Color player;
	private Field from;
	private Field to;
	private Set<String> killed;
	
	public Movement() {
		killed = new HashSet<>();
	}
	
	public Movement(Color player, Field from, Field to) {
		this();
		this.player = player;
		this.from = from;
		this.to= to;
	}

	public Color getPlayerColor() {
		return player;
	}

	public void setPlayerColor(Color player) {
		this.player = player;
	}

	public Field getFrom() {
		return from;
	}

	public void setFrom(Field from) {
		this.from = from;
	}

	public Field getTo() {
		return to;
	}

	public void setTo(Field to) {
		this.to = to;
	}

	public Set<String> getKilled() {
		return killed;
	}

	public void setKilled(Set<String> killed) {
		this.killed = killed;
	}
	
	public void addKilled(String killedOne) {
		killed.add(killedOne);
	}
}
