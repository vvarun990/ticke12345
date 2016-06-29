package com.rugden.database.objects;

import org.junit.Assert;
import org.junit.Test;

public class TestBalconyOneSection {

	@Test
	public void testDataSetup() {
		BalconyOneSection section = new BalconyOneSection();
		Assert.assertEquals("Balcony 1", section.getLevelName());
		Assert.assertEquals(Long.valueOf(50), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(100), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(15), section.getTotalRows());
		Assert.assertEquals(Integer.valueOf(1500), section.getAvailableSeats());
	}

}
