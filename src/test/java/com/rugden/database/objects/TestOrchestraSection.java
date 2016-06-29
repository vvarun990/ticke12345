package com.rugden.database.objects;

import org.junit.Assert;
import org.junit.Test;

public class TestOrchestraSection {
	
	@Test
	public void testDataSetup() {
		OrchestraSection section = new OrchestraSection();
		Assert.assertEquals("Orchestra", section.getLevelName());
		Assert.assertEquals(Long.valueOf(100), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(50), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(25), section.getTotalRows());
		Assert.assertEquals(Integer.valueOf(1250), section.getAvailableSeats());
	}
}
