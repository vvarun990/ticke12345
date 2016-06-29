package com.rugden.database.objects;

/**
 * Object representing Balcony 1 level of the performance venue
 * 
 * @author Rug Den
 *
 */
public class BalconyOneSection extends SeatSection {

	public BalconyOneSection() {
		super.setLevelName("Balcony 1");
		super.setSeatPrice(50L);
		super.setSeatsPerRow(100);
		super.setTotalRows(15);
		super.initialize();
	}

}
