package com.pasdam.kalah;

/**
 * Enumeration of board configurations (number of houses, and initial seeds count)
 * 
 * @author paco
 * @version 0.1
 */
public enum BoardType {

	/**	Kalah(6,4) */
	KALAH_6_4(6, 4),
	
	/**	Kalah(6,5) */
	KALAH_6_5(6, 5),

	/**	Kalah(6,6) */
	KALAH_6_6(6, 6);
	
	/**	Indicates the number of houses of each player */
	public final short housesCount;

	/**	Indicates the seeds that start out in each house */
	public final short initialSeeds;
	
	/**
	 * Creates an enum value with the specified parameters
	 * 
	 * @param housesCount
	 *            number of houses for each player
	 * @param initialSeeds
	 *            number of seeds to initialize a house with
	 */
	private BoardType(int housesCount, int initialSeeds) {
		this.housesCount  = (short) housesCount;
		this.initialSeeds = (short) initialSeeds;
	}
}
