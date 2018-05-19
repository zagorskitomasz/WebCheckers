package com.webcheckers.api.persistence;

import org.springframework.stereotype.Component;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.game.Board;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.moves.Position;

@Component
public class BoardConverterImpl implements BoardConverter {

	@Override
	public String boardToString(Board board) {
		
		StringBuilder state = new StringBuilder();
		
		for(int i = 0; i < board.WIDTH; i++) {
			for(int j = 0; j < board.HEIGHT; j++) {
				state.append(board.getField(new Position(i, j)));
			}
		}
		return state.toString();
	}

	@Override
	public void encodeBoard(Board board, String currentState) {
		
		char[] state = currentState.toCharArray();
		
		for(int i = 0; i < board.WIDTH; i++) {
			for(int j = 0; j < board.HEIGHT; j++) {
				
				Position position = new Position(i, j);
				char fieldState = state[i * board.WIDTH + j];
				Checker checker = buildChecker(board, position, fieldState);
				
				board.getField(position).insertChecker(checker);
			}
		}
	}

	private Checker buildChecker(Board board, Position position, char fieldState) {
		
		Checker checker = null;
		
		if(fieldState == 'b')
			checker = new Checker(Color.BLACK, board.getField(position));
		else if(fieldState == 'w')
			checker = new Checker(Color.WHITE, board.getField(position));
		if(fieldState == 'B') {
			checker = new Checker(Color.BLACK, board.getField(position));
			checker.promote();
		}
		else if(fieldState == 'W') {
			checker = new Checker(Color.WHITE, board.getField(position));
			checker.promote();
		}
		return checker;
	}
}
