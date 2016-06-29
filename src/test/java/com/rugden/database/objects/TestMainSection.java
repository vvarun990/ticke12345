package com.rugden.database.objects;

import org.junit.Assert;
import org.junit.Test;

public class TestMainSection {
	
	@Test
	public void testDataSetup() {
		MainSection section = new MainSection();
		Assert.assertEquals("Main", section.getLevelName());
		Assert.assertEquals(Long.valueOf(75), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(100), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(20), section.getTotalRows());
		Assert.assertEquals(Integer.valueOf(2000), section.getAvailableSeats());
	}


}
