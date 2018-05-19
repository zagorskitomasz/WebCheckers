package com.webcheckers.api.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.persistence.domain.LightGame;
import com.webcheckers.api.persistence.domain.LightGameFactory;
import com.webcheckers.api.service.GameID;

@Component
public class GamePersisterImpl implements GamePersister{

	@Autowired
	private BoardConverter converter;
	
	@Autowired
	private RestClient restClient;
	
	@Autowired
	private LightGameFactory lightGameFactory;
	
	@Override
	synchronized public void syncWithDB(GameID gameID, Game game) {
		
		try {
			LightGame lightGame = restClient.loadGame(Integer.parseInt(gameID.NAME));
			
			if(lightGame.getState() != null) {
				
				String state = lightGame.getState();
				converter.encodeBoard(game.getBoard(), state.substring(2, state.length()));
				game.setMover(Color.getColorBySymbol(state.substring(0, 1)));
			}
			else
				saveState(gameID, game);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@Async
	synchronized public void saveState(GameID gameID, Game game) {
		
		LightGame lightGame = lightGameFactory.create(gameID.NAME, game);
		
		if(lightGame == null)
			return;
		
		restClient.saveGame(lightGame);
	}

	@Override
	@Async
	synchronized public void archivise(GameID gameID, Game game) {

		LightGame lightGame = lightGameFactory.create(gameID.NAME, game);
		
		if(lightGame == null)
			return;
		
		lightGame.setWinner(Color.BLACK.equals(game.whoWon().getColor()) ? "B" : "W");
		
		restClient.saveGame(lightGame);
	}
}
