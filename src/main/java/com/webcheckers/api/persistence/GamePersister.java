package com.webcheckers.api.persistence;

import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.service.GameID;

public interface GamePersister {

	public void syncWithDB(GameID gameID, Game game);
	public void saveState(GameID gameID, Game game);
	public void archivise(GameID gameID, Game game);
	public boolean gameExists(String nAME);
}
