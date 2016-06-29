package com.rugden.database.objects;

import org.junit.Assert;
import org.junit.Test;

import com.rugden.database.objects.SeatSection;

public class TestSeatSection {

	@Test
	public void testDataSetup() {
		SeatSection section = new SeatSection();
		Assert.assertEquals(Long.valueOf(0), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(0), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(0), section.getTotalRows());
		Assert.assertEquals(Integer.valueOf(0), section.getTotalSeats());
		Assert.assertEquals(Integer.valueOf(0), section.getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(0), section.getHeldSeats());
		Assert.assertEquals(Integer.valueOf(0), section.getReservedSeats());

	}

	@Test
	public void testGettersAndSetters() {
		SeatSection section = new SeatSection();
		section.setLevelName("Main");
		section.setSeatPrice(75L);
		section.setSeatsPerRow(20);
		section.setTotalRows(50);

		Assert.assertEquals("Main", section.getLevelName());
		Assert.assertEquals(Long.valueOf(75), section.getSeatPrice());
		Assert.assertEquals(Integer.valueOf(20), section.getSeatsPerRow());
		Assert.assertEquals(Integer.valueOf(50), section.getTotalRows());

	}

	@Test
	public void testPerformHoldWhenSeatsToHoldLessThanAvailable() {
		SeatSection section = getMockSection();
		section.initialize();
		Integer status = section.holdSeats(100);
		Assert.assertEquals(Integer.valueOf(0), status);
		Assert.assertEquals(Integer.valueOf(100), section.getHeldSeats());
		Assert.assertEquals(Integer.valueOf(2000), section.getTotalSeats());
		Assert.assertEquals(Integer.valueOf(2000 - 100), section.getAvailableSeats());
	}

	@Test
	public void testPerformHoldWhenSeatsToHoldEqualToAvailable() {
		SeatSection section = getMockSection();
		Integer status = section.holdSeats(2000);
		Assert.assertEquals(Integer.valueOf(0), status);
		Assert.assertEquals(Integer.valueOf(0), section.getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(2000), section.getHeldSeats());
	}

	@Test
	public void testPerformHoldWhenSeatsToHoldGreaterThanAvailable() {
		SeatSection section = getMockSection();
		Integer status = section.holdSeats(3000);
		Assert.assertEquals(Integer.valueOf(-1000), status);
		Assert.assertEquals(Integer.valueOf(0), section.getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(2000), section.getHeldSeats());
	}

	@Test
	public void testRemoveHold() {
		SeatSection section = getMockSection();

		section.holdSeats(2000);
		Assert.assertEquals(Integer.valueOf(0), section.getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(2000), section.getHeldSeats());

		section.removeSeatsHold(2000);
		Assert.assertEquals(Integer.valueOf(2000), section.getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(0), section.getHeldSeats());
	}
	
	@Test
	public void testPerformSeatReservation() {
		SeatSection section = getMockSection();
		
		section.setAvailableSeats(Integer.valueOf(0));
		section.setHeldSeats(Integer.valueOf(200));

		section.reserveSeats(200);
		
		Assert.assertEquals(Integer.valueOf(0), section.getHeldSeats());
		Assert.assertEquals(Integer.valueOf(200), section.getReservedSeats());
		
		
	}

	private SeatSection getMockSection() {
		SeatSection section = new SeatSection();
		section.setLevelName("test Section");
		section.setSeatPrice(75L);
		section.setSeatsPerRow(100);
		section.setTotalRows(20);
		section.setAvailableSeats(2000);
		return section;
	}

}
