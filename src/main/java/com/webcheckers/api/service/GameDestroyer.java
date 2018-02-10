package com.webcheckers.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.messages.Message;

@Component
public class GameDestroyer implements Runnable {

	public static final long RUN_INTERVAL = 60000;
	public static final long TIME_OUT_VALUE = 600000;
	
	private Map<GameID, Game> games;
	
	@Autowired
	private MessageService messageService;
	
	public void initialize(Map<GameID, Game> games){
		
		this.games = games;
	}
	
	@Override
	public void run() {
		
		seekAndDestroy();
	}

	private void seekAndDestroy() {
		
		for(GameID gameID : games.keySet()) {
			if(gameID.isTimedOut()) {
				
				notifyAboutDestroying(gameID);
				games.remove(gameID);
			}
		}
	}

	private void notifyAboutDestroying(GameID gameID) {
		
		Message message = new Message(MsgCode.TIME_OUT, gameID, (String[])null);
		messageService.notifyBoth(message);
	}
}
