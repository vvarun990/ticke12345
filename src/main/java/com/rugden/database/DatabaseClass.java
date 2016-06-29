package com.rugden.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rugden.constants.Constants;
import com.rugden.database.objects.BalconyOneSection;
import com.rugden.database.objects.BalconyTwoSection;
import com.rugden.database.objects.MainSection;
import com.rugden.database.objects.OrchestraSection;
import com.rugden.database.objects.SeatSection;

/**
 * Object representing performance venue with all levels and related information
 * 
 * @author Rug Den
 *
 */
public class DatabaseClass {

	private Map<Integer, SeatSection> venue = new ConcurrentHashMap<Integer, SeatSection>();
	private SeatSection level_1 = new OrchestraSection();
	private SeatSection level_2 = new MainSection();
	private SeatSection level_3 = new BalconyOneSection();
	private SeatSection level_4 = new BalconyTwoSection();

	public Map<Integer, SeatSection> getVenue() {
		this.venue.put(Constants.INT_1, level_1);
		this.venue.put(Constants.INT_2, level_2);
		this.venue.put(Constants.INT_3, level_3);
		this.venue.put(Constants.INT_4, level_4);
		return this.venue;
	}
}
