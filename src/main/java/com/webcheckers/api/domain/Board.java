package com.webcheckers.api.domain;

import java.util.LinkedList;
import java.util.List;

public class Board {

	private Position[][] board;
	private final int WIDTH = 10;
	private final int HEIGHT = 10;
	
	public Board() {
		board = new Position[WIDTH][HEIGHT];
		initializeBoard();
	}
	
	private void initializeBoard() {
		createPositions();
		fillWithCheckers();
	}

	private void createPositions() {
		
		for(int i = 0; i < WIDTH; i++)
			for(int j = 0; j < HEIGHT; j++)
				board[i][j] = new Position(i, j);
	}
	
	private void fillWithCheckers() {
		
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				decideIfChecker(i, j);
			}
		}
	}

	private void decideIfChecker(int i, int j) {
		if(j < 3) {
			if((i + j) % 2 == 0) {
				insertWhiteChecker(board[i][j]);
			}
		}
		else if(j > 6) {
			if((i + j) % 2 == 0) {
				insertBlackChecker(board[i][j]);
			}
		}
	}

	private void insertBlackChecker(Position position) {
		insertChecker(position, Color.BLACK);
	}

	private void insertWhiteChecker(Position position) {
		insertChecker(position, Color.WHITE);
	}
	
	private void insertChecker(Position position, Color color) {
		Checker checker = new Checker(color, position);
		position.insertChecker(checker);
	}
	
	public Position getPosition(int x, int y) {
		return board[x][y];
	}
	
	public boolean hasChecker(int x, int y) {
		return board[x][y].hasChecker();
	}
	
	public Checker getChecker(int x, int y) {
		return board[x][y].getChecker();
	}

	public List<Checker> getAllPlayerCheckers(Color color) {
		
		List<Checker> colorCheckers = new LinkedList<>();
		
		for(int i = 0; i < WIDTH; i ++) {
			for(int j = 0; j < HEIGHT; j++) {
				Position position = board[i][j];
				if(position.hasChecker() && position.getChecker().COLOR == color)
					colorCheckers.add(position.getChecker());
			}
		}
		return colorCheckers;
	}
	
	@Override
	public String toString() {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				stringBuilder.append(board[j][i] + " ");
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}
}
