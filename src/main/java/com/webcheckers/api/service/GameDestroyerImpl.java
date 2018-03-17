package com.webcheckers.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webcheckers.api.domain.enums.MsgCode;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.messages.Message;

@Service
public class GameDestroyerImpl implements GameDestroyer {

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
				
				try {
				notifyAboutDestroying(gameID);
				games.remove(gameID);
				}
				catch(Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	private void notifyAboutDestroying(GameID gameID) {
		
		Message message = new Message(MsgCode.TIME_OUT, gameID, (String[])null);
		messageService.notifyBoth(message);
	}
}
