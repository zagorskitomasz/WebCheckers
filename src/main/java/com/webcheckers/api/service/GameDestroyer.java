package com.webcheckers.api.service;

import java.util.Map;

import com.webcheckers.api.domain.game.Game;

public interface GameDestroyer extends Runnable {

	public void initialize(Map<GameID, Game> games);
}
