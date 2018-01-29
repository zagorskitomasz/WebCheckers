package com.webcheckers.api.domain.moves;

import java.util.LinkedList;
import java.util.List;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Field;

public class Movement {
	
	private Color player;
	private Field from;
	private Field to;
	private List<Checker> killed;
	private MoveResult state;
	
	public Movement() {
		killed = new LinkedList<>();
	}
	
	public Movement(Color player, Field from, Field to) {
		this();
		this.player = player;
		this.from = from;
		this.to= to;
		state = null;
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

	public List<Checker> getKilled() {
		return killed;
	}

	public void setKilled(List<Checker> killed) {
		this.killed = killed;
	}
	
	public void addKilled(Checker killedOne) {
		killed.add(killedOne);
	}
	
	public void setState(MoveResult state) {
		this.state = state;
	}
	
	public MoveResult getState() {
		return state;
	}

	public void checkPromote() {
		
		if(whitePromote() || blackPromote())
			promote();
	}
	
	private boolean whitePromote() {
		
		return player == Color.WHITE && to.POSITION.Y == 9;
	}
	
	private boolean blackPromote() {
		
		return player == Color.BLACK && to.POSITION.Y == 0;
	}
	
	private void promote() {
		
		if(from.hasChecker())
			from.getChecker().promote();
	}
}
