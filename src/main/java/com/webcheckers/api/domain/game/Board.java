package com.webcheckers.api.domain.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.moves.Position;

public class Board {

	private Field[][] board;
	private final int WIDTH = 10;
	private final int HEIGHT = 10;
	private List<Checker> allCheckers;
	private Random generator;
	
	public Board() {
		generator = new Random();
		board = new Field[WIDTH][HEIGHT];
		initializeBoard();
	}
	
	private void initializeBoard() {
		createFields();
		fillWithCheckers();
	}

	private void createFields() {
		
		for(int i = 0; i < WIDTH; i++)
			for(int j = 0; j < HEIGHT; j++)
				board[i][j] = new Field(i, j);
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

	private void insertBlackChecker(Field field) {
		insertChecker(field, Color.BLACK);
	}

	private void insertWhiteChecker(Field field) {
		insertChecker(field, Color.WHITE);
	}
	
	private void insertChecker(Field field, Color color) {
		Checker checker = new Checker(color, field);
		field.insertChecker(checker);
	}
	
	public Field getField(Position position) {
		return board[position.X][position.Y];
	}
	
	public boolean hasChecker(Position position) {
		return board[position.X][position.Y].hasChecker();
	}
	
	public Checker getChecker(Position position) {
		return board[position.X][position.Y].getChecker();
	}

	public List<Checker> getAllCheckers() {
		
		List<Checker> allCheckers = new LinkedList<>();
		
		for(int i = 0; i < WIDTH; i ++) {
			for(int j = 0; j < HEIGHT; j++) {
				Field field = board[i][j];
				if(field.hasChecker())
					allCheckers.add(field.getChecker());
			}
		}
		return allCheckers;
	}

	public List<Checker> getAllPlayerCheckers(Color color) {
		
		List<Checker> colorCheckers = getAllCheckers();
		
		colorCheckers.removeIf(checker -> checker.COLOR != color);
		
		return colorCheckers;
	}
	
	public Checker getRandomChecker() {
		
		if(allCheckers == null) {
			allCheckers = getAllPlayerCheckers(Color.BLACK);
			allCheckers.addAll(getAllPlayerCheckers(Color.WHITE));
		}
		return allCheckers.get(generator.nextInt(allCheckers.size()));
	}
	
	public Field getRandomEmptyField() {
		
		Field field;
		do {
			field = board[generator.nextInt(WIDTH)][generator.nextInt(HEIGHT)];
		}
		while((field.POSITION.X + field.POSITION.Y) % 2 != 0 || field.hasChecker());
		
		return field;
	}
	
	@Override
	public String toString() {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("  0 1 2 3 4 5 6 7 8 9\n");
		for(int i = 0; i < 10; i++) {
			stringBuilder.append(i + " ");
			for(int j = 0; j < 10; j++) {
				stringBuilder.append(board[j][i] + " ");
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}
}
