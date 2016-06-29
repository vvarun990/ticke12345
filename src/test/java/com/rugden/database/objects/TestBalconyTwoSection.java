package com.rugden.database.objects;

import org.junit.Assert;
import org.junit.Test;

public class TestBalconyTwoSection {
	
	@Test
	public void testDataSetup() {
		BalconyTwoSection section = new BalconyTwoSection();
		Assert.assertEquals("Balcony 2", section.getLevelName());
		Assert.assertEquals(Long.valueOf(40), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(100), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(15), section.getTotalRows());
		Assert.assertEquals(Integer.valueOf(1500), section.getAvailableSeats());
	}

}
