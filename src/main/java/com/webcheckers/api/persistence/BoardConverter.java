package com.webcheckers.api.persistence;

import com.webcheckers.api.domain.game.Board;

public interface BoardConverter {

	public String boardToString(Board board);
	public void encodeBoard(Board board, String currentState);
}
