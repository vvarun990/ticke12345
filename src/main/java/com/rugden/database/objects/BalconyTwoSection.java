package com.rugden.database.objects;

/**
 * Object representing Balcony 2 level of the performance venue
 * 
 * @author Rug Den
 *
 */
public class BalconyTwoSection extends SeatSection {

	public BalconyTwoSection() {
		super.setLevelName("Balcony 2");
		super.setSeatPrice(40L);
		super.setSeatsPerRow(100);
		super.setTotalRows(15);
		super.initialize();
	}

}
