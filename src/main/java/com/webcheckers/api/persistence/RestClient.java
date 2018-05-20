package com.webcheckers.api.persistence;

import com.webcheckers.api.persistence.domain.GameStarter;
import com.webcheckers.api.persistence.domain.LightGame;

public interface RestClient {

	public LightGame loadGame(int id);
	public boolean saveGame(LightGame game);
	
	/**
	 * Method wakes up REST API, it returns true if API is available.
	 */
	public boolean wakeUp();
	public boolean createGame(GameStarter gameStarter);
}
