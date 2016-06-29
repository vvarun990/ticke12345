package com.rugden.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class TestSeatHold {
	
	@Test
	public void testGetTotalPrice(){
		SeatHold hold = new SeatHold();
		Map<Integer, Integer> holdsPerLevel = new HashMap<Integer, Integer>();
		holdsPerLevel.put(1, 10);
		holdsPerLevel.put(2, 20);
		holdsPerLevel.put(3, 30);
		holdsPerLevel.put(4, 40);
		hold.setEmail("reston@gmail.com");
		hold.setSeatHoldId(Integer.valueOf(1));
		hold.setHoldsPerLevel(holdsPerLevel);
		hold.setPrice(10l);
		
		Assert.assertEquals(Long.valueOf(10), hold.getPrice()); 
		Assert.assertEquals("reston@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1), hold.getSeatHoldId());
		Assert.assertNotNull(hold.getHoldsPerLevel());
	}

}
