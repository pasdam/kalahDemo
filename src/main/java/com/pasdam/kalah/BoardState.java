package com.pasdam.kalah;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * This class represents the state of a Kalah board
 *
 * @author paco
 * @version 0.1
 */
public class BoardState {

	/**	Houses on the board. It has the following structure: [player 0 houses] [player 0 store] [player 1 houses] [player 1 store] */
	private final short[] houses;
	
	/**	Offset of the player 0's store in {@link #houses} */
	private final int player0StoreOffset;
	
	/**	Offset of the player 0's houses in {@link #houses} */
	private final int player1HousesOffset;
	
	/**	Offset of the player 1's store in {@link #houses} */
	private final int player1StoreOffset;
	
	/**	Indicates the index of the current player (0 or 1) */
	private int currentPlayer;
	
	/**	Total number of seeds on the board */
	private int totalSeeds;

	/**
	 * Creates a board for the specified type
	 *
	 * @param type
	 *            type of the board to use
	 * @param startingPlayer
	 *            index of the starting player (0 or 1)
	 * @throws InvalidParameterException
	 *             if the player's index is invalid
	 * @throws NullPointerException
	 *             if the type is null
	 */
	public BoardState(BoardType type, int startingPlayer) throws InvalidParameterException, NullPointerException {
		if (startingPlayer == 0 || startingPlayer == 1) {
			this.currentPlayer = startingPlayer;
			this.houses        = new short[type.housesCount * 2 + 2];
			this.totalSeeds    = type.housesCount * type.initialSeeds * 2;
			
			// initialize offsets
			this.player0StoreOffset  = this.houses.length/2 - 1;
			this.player1HousesOffset = this.player0StoreOffset + 1;
			this.player1StoreOffset  = this.houses.length -1;
			
			// initialize houses counters
			int i;
			for (i = 0; i < this.player0StoreOffset; i++) {
				this.houses[i] = type.initialSeeds;
			}
			for (i = this.player1HousesOffset; i < this.player1StoreOffset; i++) {
				this.houses[i] = type.initialSeeds;
			}
		
		} else {
			throw new InvalidParameterException("Invalid player's index: " + startingPlayer);
		}
	}
	
	/**
	 * Returns the index of the current player (0 or 1)
	 * 
	 * @return the index of the current player (0 or 1)
	 */
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	/**
	 * Returns the houses array of the player 0
	 * 
	 * @return the houses array of the player 0
	 */
	public short[] getPlayer0Houses() {
		return Arrays.copyOfRange(this.houses, 0, this.player0StoreOffset);
	}

	/**
	 * Returns the score of the player 0
	 * 
	 * @return the score of the player 0
	 */
	public short getPlayer0Score() {
		return this.houses[this.player0StoreOffset];
	}

	/**
	 * Returns the houses array of the player 1
	 * 
	 * @return the houses array of the player 1
	 */
	public short[] getPlayer1Houses() {
		return Arrays.copyOfRange(this.houses, this.player1HousesOffset, this.player1StoreOffset);
	}

	/**
	 * Returns the score of the player 1
	 * 
	 * @return the score of the player 1
	 */
	public short getPlayer1Score() {
		return this.houses[this.player1StoreOffset];
	}
	
	/**
	 * Returns true if the game is completed (seeds are all in the stores), false otherwise
	 * 
	 * @return true if the game is completed (seeds are all in the stores), false otherwise
	 */
	public boolean isCompleted() {
		return this.totalSeeds == (this.houses[this.player0StoreOffset] + this.houses[this.player1StoreOffset]);
	}
	
	/**
	 * Move the seeds from the specified house
	 * 
	 * @param house
	 *            index of the house from which move the seeds
	 * @return true if the move was performed (the specified house contained
	 *         seeds), false otherwise
	 * @throws InvalidParameterException
	 *             if the house index is invalid
	 */
	protected boolean move(int house) throws InvalidParameterException {
		if ( 0 <= house && house < (this.houses.length/2 - 1)) {
			boolean player0 = this.currentPlayer == 0;
			house += player0 ? 0 : this.player1HousesOffset;
			int seeds = this.houses[house];
			this.houses[house] = 0;
			
			// sowing seeds (exept last one)
			while (seeds > 1) {
				house = (house + 1) % this.houses.length;
				this.houses[house]++;
				seeds--;
			}
			
			// handle last seed
			if (seeds == 1) {
				house = (house + 1) % this.houses.length;
				this.houses[house]++;
				this.currentPlayer = (this.currentPlayer + 1) % 2;
				
				if ((player0 && house == this.player0StoreOffset) || (!player0 && house == this.player1StoreOffset)) {
					this.currentPlayer = (this.currentPlayer + 1) % 2;
					
				} else if (house != this.player0StoreOffset && house != this.player1StoreOffset && this.houses[house] == 1) {
					int otherPlayerHouse;
					if (house < this.player0StoreOffset) {
						otherPlayerHouse = this.player1StoreOffset - 1 - house;
					} else {
						otherPlayerHouse = house - 2 * (house - this.player0StoreOffset);
					}
					
					if (((player0 && 0 <= house && house < this.player0StoreOffset) 
								|| (!player0 && this.player1HousesOffset <= house && house < this.player1StoreOffset))
							&& this.houses[otherPlayerHouse] > 0) {
						this.houses[player0 ? this.player0StoreOffset : this.player1StoreOffset] += 1 + this.houses[otherPlayerHouse];
						this.houses[house]            = 0;
						this.houses[otherPlayerHouse] = 0;
					}
				}
				
				int limit = player0 ? this.player0StoreOffset : this.player1StoreOffset;
				int sum = 0;
				for (int i = player0 ? 0 : this.player1HousesOffset; i < limit; i++) {
					sum += this.houses[i];
				}
				if (sum == 0) {
					// the game is over: the player has moved his last seed(s)
					int storeOffset = player0 ? this.player1StoreOffset : this.player0StoreOffset;
					limit = player0 ? this.player1StoreOffset : this.player0StoreOffset;
					for (int i = player0 ? this.player1HousesOffset : 0; i < limit; i++) {
						this.houses[storeOffset] += this.houses[i];
						this.houses[i] = 0;
					}
				}
				
				return true;
			
			} else {
				// house is empty
				return false;
			}
		} else {
			throw new InvalidParameterException("Invalid house index: " + house);
		}
	}
}
