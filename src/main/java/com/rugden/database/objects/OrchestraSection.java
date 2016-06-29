package com.rugden.database.objects;

/**
 * Object representing Orchestra level of the performance venue
 * 
 * @author Rug Den
 *
 */
public class OrchestraSection extends SeatSection {

	public OrchestraSection() {
		super.setLevelName("Orchestra");
		super.setSeatPrice(100L);
		super.setSeatsPerRow(50);
		super.setTotalRows(25);
		super.initialize();
	}

}
