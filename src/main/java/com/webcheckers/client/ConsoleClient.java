package com.webcheckers.client;

import java.util.Scanner;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.game.Board;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Field;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.moves.MovementValidator;
import com.webcheckers.api.domain.moves.Position;

public class ConsoleClient {

	public static void main(String[] args) {
		
		Board board = new Board();
		
		Player white = new Player("White");
		white.initialize(Color.WHITE, null);
		
		Player black = new Player("Black");
		black.initialize(Color.BLACK, null);
		
		for(int i = 0; i < 50; i++) {
			
			Checker checker = board.getRandomChecker();
			Field field = board.getRandomEmptyField();
			checker.moveTo(field);
			if(i % 10 == 0)
				checker.promote();
		}
		
		System.out.println(board);
		MovementValidator validator = new MovementValidator(board);
		
		int x = 0;
		int y = 0;
		Position position = new Position(x, y);
		System.out.println("Black start with " + x + ", " + y + ": " + validator.canIStartWith(black, position));
		
		if(validator.getPossibilities() != null) {
			System.out.println("\nBlack possibilities:");
			validator.getPossibilities().forEach(System.out::println);
		}
		
		System.out.println("\nWhite start with " + x + ", " + y + ": " + validator.canIStartWith(white, position));
		
		if(validator.getPossibilities() != null) {
			System.out.println("\nWhite possibilities:");
			validator.getPossibilities().forEach(System.out::println);
		}
		
		Scanner scanner = new Scanner(System.in);
		String input;
		do {
			input = scanner.nextLine();
			if("0".equals(input))
				break;
			
			String[] inputs = input.split(" ");
			
			Player movingPlayer = "b".equals(inputs[0]) ? black : white;
			
			int xFrom = Integer.valueOf(inputs[1]);
			int yFrom = Integer.valueOf(inputs[2]);
			
			int xTo = Integer.valueOf(inputs[3]);
			int yTo = Integer.valueOf(inputs[4]);
			
			Position from = new Position(xFrom, yFrom);
			Position to = new Position(xTo, yTo);
			
			System.out.println(movingPlayer.getColor() + " from " + from.X + ", " + from.Y + " to " + to.X + ", " + to.Y + ": " + validator.canIMoveThere(movingPlayer, from, to));
		}
		while(true);
		scanner.close();
	}
}
