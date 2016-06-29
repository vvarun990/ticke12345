package com.rugden.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import com.rugden.database.DatabaseClass;
import com.rugden.model.SeatHold;

public class TestTicketService {
	Map<String, SeatHold> bookings = new HashMap<String, SeatHold>();
	Map<String, String> reservations = new HashMap<String, String>();
	DatabaseClass db = new DatabaseClass();

	@Test
	public void testGetAvailableTickets() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> venueLevel = Optional.empty();
		int seats = service.numSeatsAvailable(venueLevel);
		Assert.assertEquals(6250, seats);
	}

	@Test
	public void testGetAvailableTicketsByLevel() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> venueLevel = Optional.of(1);
		int seats = service.numSeatsAvailable(venueLevel);
		Assert.assertEquals(1250, seats);
	}

	@Test
	public void testBookingsAreSavedOnSeatHold() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		Map<String, SeatHold> testBookings = service.getBookings();
		testBookings.containsKey("rugden@gmail.com");
		testBookings.containsValue(hold);
	}

	@Test
	public void testSeatsAreHeldOnOneLevel() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
	}

	@Test
	public void testGetTotalPriceAfterHold() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(2), level, level, "rugden@gmail.com");
		Assert.assertEquals(Long.valueOf(200), hold.getPrice());
	}

	@Test
	public void testHoldSeatsWhenNoAvailableSeatsAtRequestedLevelButAvailableInOthers() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));

		service.findAndHoldSeats(Integer.valueOf(1250), level, level, "reston@gmail.com");
		Assert.assertFalse(service.getBookings().containsKey("reston@gmail.com"));
	}

	@Test
	public void testHoldSeatsOnTwoLevels() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level_1 = Optional.of(1);
		Optional<Integer> level_2 = Optional.of(2);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000), level_1, level_2, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
	}

	@Test
	public void testHoldSeatsOnMultipleLevels() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level_1 = Optional.of(1);
		Optional<Integer> level_4 = Optional.of(4);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), level_1, level_4,
				"rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));
	}

	@Test
	public void testHoldSeatsByMinimumLevel() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> minLevel = Optional.of(1);
		Optional<Integer> level_2 = Optional.of(0);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), minLevel, level_2,
				"rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));
	}

	@Test
	public void testHoldSeatsByMaximumLevel() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> maxLevel = Optional.of(4);
		Optional<Integer> level = Optional.of(0);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), level, maxLevel,
				"rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));
	}

	@Test
	public void testHoldSeatsWithNoMinOrMax() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(0);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), level, level, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));
	}

	@Test
	public void testAvailableCheckAfterAllTicketsBooked() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level_1 = Optional.of(1);
		Optional<Integer> level_3 = Optional.of(4);

		// user 1
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), level_1, level_3,
				"rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));

		// user 2
		Optional<Integer> venueLevel = Optional.empty();
		int seats = service.numSeatsAvailable(venueLevel);
		Assert.assertEquals(0, seats);
	}

	@Test
	public void testHoldAfterAllTicketsBooked() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level_1 = Optional.of(1);
		Optional<Integer> level_3 = Optional.of(4);

		// user 1
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250 + 2000 + 3000), level_1, level_3,
				"rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1250), hold.getHoldsPerLevel().get(1));
		Assert.assertEquals(Integer.valueOf(2000), hold.getHoldsPerLevel().get(2));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(3));
		Assert.assertEquals(Integer.valueOf(1500), hold.getHoldsPerLevel().get(4));

		// user 2
		service.findAndHoldSeats(Integer.valueOf(1), level_1, level_3, "reston@gmail.com");
		Assert.assertFalse(service.getBookings().containsKey("reston@gmail.com"));
	}

	@Test
	public void testHoldOnOneLevelAfterSomeAreBookedAndRequestedEqualsAvailable() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);

		// user 1
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(250), level, level, "rugden@gmail.com");
		Integer numHolds = service.getBookings().get("rugden@gmail.com").getHoldsPerLevel().get(1);
		Assert.assertEquals(Integer.valueOf(250), numHolds);
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(250), hold.getHoldsPerLevel().get(1));

		// user 2
		hold = service.findAndHoldSeats(Integer.valueOf(1000), level, level, "reston@gmail.com");
		numHolds = service.getBookings().get("reston@gmail.com").getHoldsPerLevel().get(1);
		Assert.assertEquals(Integer.valueOf(1000), numHolds);
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("reston@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1000), hold.getHoldsPerLevel().get(1));
	}

	@Test
	public void testHoldOnFirstLevelAfterSomeAreBookedAndRequestedExceedsAvailable() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);

		// user 1
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(250), level, level, "rugden@gmail.com");
		Integer numHolds = service.getBookings().get("rugden@gmail.com").getHoldsPerLevel().get(1);
		Assert.assertEquals(Integer.valueOf(250), numHolds);
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(250), hold.getHoldsPerLevel().get(1));

		// user 2
		hold = service.findAndHoldSeats(Integer.valueOf(1100), level, level, "reston@gmail.com");
		numHolds = service.getBookings().get("reston@gmail.com").getHoldsPerLevel().get(1);
		Assert.assertEquals(Integer.valueOf(1000), numHolds);
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("reston@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(1000), hold.getHoldsPerLevel().get(1));
	}

	@Test
	public void testHoldSeatsWithExistingBooking() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		// first booking
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(700), level, level, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(700), hold.getHoldsPerLevel().get(1));

		// second booking
		hold = service.findAndHoldSeats(Integer.valueOf(700), level, level, "rugden@gmail.com");
		Integer numHolds = service.getBookings().get("rugden@gmail.com").getHoldsPerLevel().get(1);
		Assert.assertEquals(Integer.valueOf(700), numHolds);
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(700), hold.getHoldsPerLevel().get(1));
	}

	@Test
	public void testGenerateConfirmationCodeOnReserveSeats() {
		bookings.put("reston@gmail.com", getMockSeatHold());
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		service.getCache().put("reston@gmail.com", getMockSeatHold());
		String code = service.reserveSeats(1, "reston@gmail.com");
		Assert.assertEquals("S1reston@2016", code);
	}

	@Test
	public void testReservationsArePersistedInMap() {
		bookings.put("reston@gmail.com", getMockSeatHold());
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		service.getCache().put("reston@gmail.com", getMockSeatHold());
		service.reserveSeats(1, "reston@gmail.com");
		Map<String, String> testReservations = service.getReservations();
		Assert.assertTrue(testReservations.containsKey("reston@gmail.com"));
		Assert.assertTrue(testReservations.containsValue("S1reston@2016"));
	}

	@Test
	public void testReservationsArePersistedInDatabase() {
		bookings.put("reston@gmail.com", getMockSeatHold());
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		service.getCache().put("reston@gmail.com", getMockSeatHold());
		service.reserveSeats(1, "reston@gmail.com");
		Assert.assertEquals(Integer.valueOf(10), service.getVenueDatabase().get(1).getReservedSeats());
		Assert.assertEquals(Integer.valueOf(20), service.getVenueDatabase().get(2).getReservedSeats());
		Assert.assertEquals(Integer.valueOf(30), service.getVenueDatabase().get(3).getReservedSeats());
		Assert.assertEquals(Integer.valueOf(40), service.getVenueDatabase().get(4).getReservedSeats());
	}

	@Test
	public void testHoldIsRemovedFromMapAfterReservation() {
		bookings.put("reston@gmail.com", getMockSeatHold());
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		service.getCache().put("reston@gmail.com", getMockSeatHold());
		service.reserveSeats(1, "reston@gmail.com");
		Map<String, SeatHold> testBookings = service.getBookings();
		Assert.assertFalse("Hold should have been removed", testBookings.containsKey("reston@gmail.com"));
	}

	@Test
	public void testReservationWithNoHold() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		String code = service.reserveSeats(1, "reston@gmail.com");
		Assert.assertEquals("NoReservation", code);
		Assert.assertTrue(service.getReservations().isEmpty());
	}

	@Test
	public void testRemoveHolds() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		Assert.assertNotNull("Should not be null", hold);
		Assert.assertEquals("rugden@gmail.com", hold.getEmail());
		Assert.assertEquals(Integer.valueOf(0), service.getVenueDatabase().get(1).getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(1250), service.getVenueDatabase().get(1).getHeldSeats());

		service.removeHold(hold.getHoldsPerLevel(), service.getVenueDatabase());
		Assert.assertEquals(Integer.valueOf(0), service.getVenueDatabase().get(1).getHeldSeats());
		Assert.assertEquals(Integer.valueOf(1250), service.getVenueDatabase().get(1).getAvailableSeats());
	}

	@Test
	public void testCacheEviction() throws InterruptedException, ExecutionException {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		service.getCache().put("rugden@gmail.com", getMockSeatHold());
		SeatHold hold = service.getCache().getIfPresent("rugden@gmail.com");
		Assert.assertNotNull(hold.getEmail());
		Thread.sleep(6000);
		service.getCache().cleanUp();
		hold = service.getCache().getIfPresent("rugden@gmail.com");
		Assert.assertNull(hold);
	}
	
	@Test
	public void testCacheIsLoadedOnSeatHold() {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		SeatHold cachedHold = service.getCache().getIfPresent("rugden@gmail.com");
		Assert.assertNotNull("Should not be null", cachedHold);
	}

	@Test
	public void testSeatHoldsAreRemovedAfterHoldExpiration() throws InterruptedException {
		TicketService service = new TicketService(db.getVenue(), bookings, reservations);
		Optional<Integer> level = Optional.of(1);
		Assert.assertEquals(Integer.valueOf(1250), service.getVenueDatabase().get(1).getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(0), service.getVenueDatabase().get(1).getHeldSeats());
		service.findAndHoldSeats(Integer.valueOf(1250), level, level, "rugden@gmail.com");
		Assert.assertEquals(Integer.valueOf(0), service.getVenueDatabase().get(1).getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(1250), service.getVenueDatabase().get(1).getHeldSeats());
		Thread.sleep(6000);
		service.getCache().cleanUp();
		SeatHold cachedHold = service.getCache().getIfPresent("rugden@gmail.com");
		Assert.assertEquals(Integer.valueOf(1250), service.getVenueDatabase().get(1).getAvailableSeats());
		Assert.assertEquals(Integer.valueOf(0), service.getVenueDatabase().get(1).getHeldSeats());
		Assert.assertNull("Should not be null", cachedHold); 
	}

	private SeatHold getMockSeatHold() {
		SeatHold hold = new SeatHold();
		Map<Integer, Integer> holdsPerLevel = new HashMap<Integer, Integer>();
		holdsPerLevel.put(1, 10);
		holdsPerLevel.put(2, 20);
		holdsPerLevel.put(3, 30);
		holdsPerLevel.put(4, 40);
		hold.setEmail("reston@gmail.com");
		hold.setSeatHoldId(Integer.valueOf(1));
		hold.setPrice(0l);
		hold.setHoldsPerLevel(holdsPerLevel);
		return hold;

	}

}
