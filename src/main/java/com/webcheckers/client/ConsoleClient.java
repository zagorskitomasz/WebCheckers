package com.webcheckers.client;

import java.util.List;
import java.util.Scanner;

import com.webcheckers.api.domain.enums.Color;
import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.game.Checker;
import com.webcheckers.api.domain.game.Game;
import com.webcheckers.api.domain.game.Player;
import com.webcheckers.api.domain.game.PolishChekersGame;
import com.webcheckers.api.domain.moves.Position;

public class ConsoleClient {

	public static void main(String[] args) {
		
		Game game = new PolishChekersGame();
		
		Player playerWhite = new Player("White");
		Player playerBlack = new Player("Black");
		playerWhite.initialize(Color.WHITE, null);
		playerBlack.initialize(Color.BLACK, null);
		
		game.create(playerWhite);
		game.join(playerBlack);
		
		game.start();
		
		String[][] frontBoard = new String[10][10];
		
		addCheckers(frontBoard, game.addToBoard());
		
		Scanner scanner = new Scanner(System.in);
		String input;
		do {
			drawBoard(frontBoard);
			System.out.println(game.whoseMove().getName() + " move:");
			input = scanner.nextLine();
			if("0".equals(input))
				break;
			
			String[] inputs = input.split(" ");
			
			int x = Integer.valueOf(inputs[0]);
			int y = Integer.valueOf(inputs[1]);
			
			Position position = new Position(x, y);
			
			MoveResult result = game.move(position);
			System.out.println(result);
			
			addCheckers(frontBoard, game.addToBoard());
			removeCheckers(frontBoard, game.removeFromBoard());
			}
		while(true);
		scanner.close();
	}

	private static void removeCheckers(String[][] frontBoard, List<?> removeFromBoard) {

		for(Object object : removeFromBoard) {
			Position position = (Position)object;
			frontBoard[position.Y][position.X] = null;
		}
	}

	private static void addCheckers(String[][] frontBoard, List<?> addToBoard) {
		
		for(Object object : addToBoard) {
			Checker checker = (Checker)object;
			frontBoard[checker.getField().POSITION.Y][checker.getField().POSITION.X] = checker.toString();
		}
	}

	private static void drawBoard(String[][] frontBoard) {
		System.out.println();
		System.out.println("  0 1 2 3 4 5 6 7 8 9");
		for(int i = 0; i < 10; i ++) {
			System.out.print(i + " ");
			for(int j = 0; j < 10; j++) {
				System.out.print(frontBoard[i][j] == null ? "_" : frontBoard[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
