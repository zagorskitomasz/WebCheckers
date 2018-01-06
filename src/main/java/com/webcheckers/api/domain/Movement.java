package com.webcheckers.api.domain;

import java.util.HashSet;
import java.util.Set;

public class Movement {
	
	private Player player;
	private String from;
	private String to;
	private Set<String> killed;
	
	public Movement() {
		killed = new HashSet<>();
	}
	
	public Movement(Player player, String from, String to) {
		this();
		this.player = player;
		this.from = from;
		this.to= to;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
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
	
	@Override
	public String toString() {
		return "Player " + player + " moved from " + from + " to " + to + " killing " + killed.size() + " checkers.";
	}
}
