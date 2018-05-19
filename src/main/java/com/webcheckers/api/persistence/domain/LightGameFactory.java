package com.webcheckers.api.persistence.domain;

import com.webcheckers.api.domain.game.Game;

public interface LightGameFactory {

	public LightGame create(String ID, Game game);
}
