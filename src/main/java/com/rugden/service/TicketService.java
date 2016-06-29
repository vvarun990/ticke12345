package com.rugden.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.rugden.constants.Constants;
import com.rugden.database.objects.SeatSection;
import com.rugden.model.SeatHold;

import com.google.common.cache.*;

/**
 * 
 * @author Rug Den
 *
 */
public class TicketService implements ITicketService {

	private Map<Integer, SeatSection> venueDatabase;
	private Map<String, SeatHold> bookings;
	private Map<String, String> reservations;
	private LoadingCache<String, SeatHold> cache;

	public LoadingCache<String, SeatHold> getCache() {
		return cache;
	}

	public void setCache(LoadingCache<String, SeatHold> cache) {
		this.cache = cache;
	}

	/**
	 * initialize cache that holds seat holds.
	 */
	public void initCache() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(Constants.SEAT_HOLD_EXPIRATION, TimeUnit.SECONDS)
				.expireAfterAccess(Constants.SEAT_HOLD_EXPIRATION, TimeUnit.SECONDS)
				.removalListener(new RemovalListener<String, SeatHold>() {
					@Override
					public void onRemoval(RemovalNotification<String, SeatHold> notification) {
						removeHold(notification.getValue().getHoldsPerLevel(), getVenueDatabase());
						System.out.println("Removing the key = " + notification.getKey());

					}
				}).build(new CacheLoader<String, SeatHold>() {
					@Override
					public SeatHold load(String key) throws Exception {
						return getValueForKey(key);
					}
				});
	}

	protected SeatHold getValueForKey(String key) {
		SeatHold value = null;
		try {
			value = cache.get(key);
			System.out.println("got value from cache for '" + key + "': " + value);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	public TicketService(Map<Integer, SeatSection> venue, Map<String, SeatHold> bookings,
			Map<String, String> reservations) {
		this.venueDatabase = venue;
		this.bookings = bookings;
		this.reservations = reservations;
		initCache();

	}

	public Map<Integer, SeatSection> getVenueDatabase() {
		return venueDatabase;
	}

	public void setVenueDatabase(Map<Integer, SeatSection> venueDatabase) {
		this.venueDatabase = venueDatabase;
	}

	public Map<String, SeatHold> getBookings() {
		return bookings;
	}

	public void setBookings(Map<String, SeatHold> bookings) {
		this.bookings = bookings;
	}

	public Map<String, String> getReservations() {
		return reservations;
	}

	public void setReservations(Map<String, String> reservations) {
		this.reservations = reservations;
	}

	@Override
	public int numSeatsAvailable(Optional<Integer> venueLevel) {
		int availableSeats = Constants.ZERO;
		if (venueLevel.equals(Optional.empty())) {
			for (SeatSection section : venueDatabase.values()) {
				availableSeats = availableSeats + section.getAvailableSeats();
			}
		} else {
			availableSeats = venueDatabase.get(venueLevel.get()).getAvailableSeats();
		}
		return availableSeats;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
			String customerEmail) {
		SeatHold hold = new SeatHold();
		// if user already has an existing booking, return existing Seathold
		if (!bookings.isEmpty() && bookings.containsKey(customerEmail) && (cache.getIfPresent(customerEmail) != null)) {
			return bookings.get(customerEmail);
		}

		if (numSeatsAvailable(Optional.empty()) > Constants.ZERO) {
			Map<Integer, Integer> holdsPerLevel = new HashMap<Integer, Integer>();
			int heldSeats = Constants.ZERO;
			Integer holdStatus = Constants.ZERO;
			// if minLevel and MaxLevel have been set
			if (minLevel.get() > Constants.ZERO && maxLevel.get() > Constants.ZERO) {
				if (minLevel.equals(maxLevel)) {
					if (numSeatsAvailable(minLevel) > Constants.ZERO) {
						holdStatus = venueDatabase.get(minLevel.get()).holdSeats(numSeats);
						holdsPerLevel.put(minLevel.get(), numSeats + holdStatus);
					}
				} else {
					holdStatus = Constants.ZERO;
					for (Map.Entry<Integer, SeatSection> section : venueDatabase.entrySet()) {
						Optional<Integer> level = Optional.of(section.getKey());
						if (section.getKey() >= minLevel.get() && section.getKey() <= maxLevel.get()
								&& (numSeatsAvailable(level) > Constants.ZERO)) {
							holdSeats(holdStatus, heldSeats, numSeats, section, holdsPerLevel);
						}
					}
				}
			}
			// if only minLevel has been set
			else if (minLevel.get() > Constants.ZERO) {
				holdStatus = Constants.ZERO;
				for (Map.Entry<Integer, SeatSection> section : venueDatabase.entrySet()) {
					Optional<Integer> level = Optional.of(section.getKey());
					if (section.getKey() >= minLevel.get() && (numSeatsAvailable(level) > Constants.ZERO)) {
						holdSeats(holdStatus, heldSeats, numSeats, section, holdsPerLevel);
					}
				}
			}
			// if only max level has been set
			else if (maxLevel.get() > Constants.ZERO) {
				holdStatus = Constants.ZERO;
				for (Map.Entry<Integer, SeatSection> section : venueDatabase.entrySet()) {
					holdSeats(holdStatus, heldSeats, numSeats, section, holdsPerLevel);
				}
			}
			// if neither minLevel nor maxLevel have been set
			else {
				holdStatus = Constants.ZERO;
				for (Map.Entry<Integer, SeatSection> section : venueDatabase.entrySet()) {
					Optional<Integer> level = Optional.of(section.getKey());
					if (numSeatsAvailable(level) > Constants.ZERO) {
						holdSeats(holdStatus, heldSeats, numSeats, section, holdsPerLevel);
					}
				}
			}
			if (!holdsPerLevel.isEmpty()) {
				hold.setHoldsPerLevel(holdsPerLevel);
				hold.setEmail(customerEmail);
				hold.setPrice(getHoldPrice(holdsPerLevel, venueDatabase));
				bookings.put(customerEmail, hold);
				cache.put(customerEmail, hold);
			}
		}
		return hold;
	}

	/**
	 * Helper method to persist held seats per level in a map
	 * 
	 * @param holdStatus
	 * @param heldSeats
	 *            number of seats held
	 * @param numSeats
	 *            number of seats to hold
	 * @param section
	 *            section that has seats to hold
	 * @param holdsPerLevel
	 *            list of venue levels and associated held seats
	 */
	private void holdSeats(Integer holdStatus, int heldSeats, int numSeats, Entry<Integer, SeatSection> section,
			Map<Integer, Integer> holdsPerLevel) {
		holdStatus = section.getValue().holdSeats(numSeats);
		heldSeats = section.getValue().getHeldSeats();
		holdsPerLevel.put(section.getKey(), heldSeats);
		numSeats = Math.abs(holdStatus);
	}

	/**
	 * calculate price of tickets
	 * 
	 * @param holdsPerLevel
	 *            Numbers of seat holds per level
	 * @param venueDatabase
	 *            venue database with all information related to seat
	 *            availability
	 * @return total price of held tickets
	 */
	private long getHoldPrice(Map<Integer, Integer> holdsPerLevel, Map<Integer, SeatSection> venueDatabase) {
		long price = Constants.ZERO_LONG;
		for (Map.Entry<Integer, Integer> levelHold : holdsPerLevel.entrySet()) {
			price = levelHold.getValue() * venueDatabase.get(levelHold.getKey()).getSeatPrice();
		}
		return price;
	}

	/**
	 * remove specific holds from database
	 * 
	 * @param holdsPerLevel
	 *            list of holds to remove per level
	 * @param venueDatabase
	 *            database with all holds and related info
	 */

	public void removeHold(Map<Integer, Integer> holdsPerLevel, Map<Integer, SeatSection> venueDatabase) {
		for (Map.Entry<Integer, Integer> levelHold : holdsPerLevel.entrySet()) {
			venueDatabase.get(levelHold.getKey()).removeSeatsHold(levelHold.getValue());
		}
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		String[] codeArray = customerEmail.split(Constants.DELIMETER);
		SeatHold hold = cache.getIfPresent(customerEmail);
		if (hold != null) {
			String code = Constants.LETTER_S + seatHoldId + codeArray[0] + Constants.DELIMETER
					+ Calendar.getInstance().get(Calendar.YEAR);
			for (Map.Entry<Integer, Integer> levelHold : hold.getHoldsPerLevel().entrySet()) {
				venueDatabase.get(levelHold.getKey()).reserveSeats(levelHold.getValue());
			}
			bookings.remove(customerEmail);
			reservations.put(customerEmail, code);
			return code;
		}
		return Constants.No_RESERVATION_FOUND;
	}
}
