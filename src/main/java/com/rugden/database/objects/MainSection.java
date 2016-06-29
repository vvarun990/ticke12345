package com.rugden.database.objects;

/**
 * Object representing Main level of the performance venue
 * 
 * @author Rug Den
 *
 */
public class MainSection extends SeatSection {

	public MainSection() {
		super.setLevelName("Main");
		super.setSeatPrice(75L);
		super.setSeatsPerRow(100);
		super.setTotalRows(20);
		super.initialize();
	}

}
