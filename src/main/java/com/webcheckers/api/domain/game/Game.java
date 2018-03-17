package com.webcheckers.api.domain.game;

import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.webcheckers.api.domain.enums.MoveResult;
import com.webcheckers.api.domain.moves.Position;

/**
 * Interface of turn-based game.
 * 
 * @author tomek
 *
 */
public interface Game {
	
	/**
	 * Creating game with one pending player.
	 * 
	 * @param game creator
	 * @return creating successful
	 */
	public boolean create(Player player);
	
	/**
	 * Adding second player to game created before.
	 * 
	 * @param player
	 * @return joining successful
	 */
	public boolean join(Player player);
	
	/**
	 * Returns player that is allowed to make move.
	 * 
	 * @return
	 */
	public Player whoseMove();
	
	/**
	 * Starting created game.
	 * 
	 * @return player that can do first move
	 */
	public Player start();
	
	/**
	 * Asking game which player has won.
	 * 
	 * @return
	 */
	public Player whoWon();
	
	/**
	 * Try make move to given localization.
	 * 
	 * @param position
	 * @return
	 */
	public MoveResult move(Position position, WebSocketSession session);
	
	/**
	 * After move some items could be removed from game board.
	 * 
	 * @return list of items to be removed
	 */
	default public List<?> removeFromBoard(){
		
		return null;
	}
	
	/**
	 * After move some items could be added to game board.
	 * 
	 * @return list of items to be added
	 */
	default public List<?> addToBoard(){
		
		return null;
	}
	
	/**
	 * After move some items could be selected to be removed after further event.
	 * 
	 * @return list of items to be removed later
	 */
	default public List<?> removeFromBoardLater(){
		
		return null;
	}
	
	/**
	 * After move some item could be selected on game board.
	 * 
	 * @return selected item
	 */
	default Position getSelectedPosition() {
		
		return null;
	}
	
	/**
	 * Sometimes games have some info for players etc.
	 * 
	 * @return Game message or null if nothing interesting happened.
	 */
	default public String getMessage() {
		
		return null;
	}

	/**
	 * Returns players involved in game
	 * 
	 * @return players
	 */
	public Player[] getPlayers();
	
	public boolean containsSession(WebSocketSession session);
	
	public void removePlayerBySession(WebSocketSession session);
	
	public boolean bothPlayersJoined();

	public boolean invert(WebSocketSession session);
}
