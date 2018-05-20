package com.webcheckers.api.persistence.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.persistence.BoardConverter;

@Component
public class LightGameFactoryImpl implements LightGameFactory{

	@Autowired
	private BoardConverter converter;
	
	@Override
	public LightGame create(String ID, Game game) {
		
		try {
			LightGame lightGame = new LightGame();
			lightGame.setId(ID.hashCode());
			
			lightGame.setState(buildState(game));
			
			return lightGame;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private String buildState(Game game) {
		
		StringBuilder state = new StringBuilder();
		
		state.append(game.whoseMove().getColor().getSymbol() + " ");
		state.append(converter.boardToString(game.getBoard()));
		
		return state.toString();
	}
}
