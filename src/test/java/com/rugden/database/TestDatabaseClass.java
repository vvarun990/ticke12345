package com.rugden.database;

import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

import com.rugden.database.objects.SeatSection;

public class TestDatabaseClass {
	
	@Test
	public void testVenueDetailsAreLoadedOnInitialization() { 
		DatabaseClass db =  new DatabaseClass();
		Map<Integer, SeatSection> venue = db.getVenue();
		assertNotNull(venue);
	}

}
