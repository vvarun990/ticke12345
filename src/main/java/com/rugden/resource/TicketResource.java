package com.rugden.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.rugden.constants.Constants;
import com.rugden.database.DatabaseClass;
import com.rugden.model.SeatHold;
import com.rugden.service.TicketService;

/**
 * TicketResource is a class that exposes a RESTFul API on how to find, hold and
 * reserve tickets
 * 
 * @author Rug Den
 *
 */
@Singleton
@Path("/tickets")
public class TicketResource {
	TicketService ticketService;
	Map<String, SeatHold> bookings;
	Map<String, String> reservations;
	DatabaseClass db = new DatabaseClass();

	public TicketResource() {
		reservations = new HashMap<String, String>();
		bookings = new HashMap<String, SeatHold>();
		ticketService = new TicketService(db.getVenue(), bookings, reservations);
	}

	public TicketService getTicketService() {
		return ticketService;
	}

	public Map<String, SeatHold> getBookings() {
		return bookings;
	}

	public Map<String, String> getReservations() {
		return reservations;
	}

	/**
	 * Find all available seats
	 * 
	 * @return all available seats
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAllAvailableTickets() {
		Optional<Integer> venueLevel = Optional.empty();
		return String.valueOf(ticketService.numSeatsAvailable(venueLevel));
	}

	/**
	 * Find all available seats on a specific level
	 * 
	 * @param levelId
	 *            level on which to find available seats
	 * @return all available seats on a level
	 */
	@GET
	@Path("/{venueLevel}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAvailableTicketsByLevel(@PathParam("venueLevel") int levelId) {

		if (levelId < Constants.INT_1 || levelId > Constants.INT_4) {
			return Constants.INVALID_LEVEL;
		} else {
			Optional<Integer> venueLevel = Optional.of(levelId);
			return String.valueOf(ticketService.numSeatsAvailable(venueLevel));
		}
	}

	/**
	 * Find and hold the best available seats for a customer
	 * 
	 * @param numSeats
	 *            Find and hold the best available seats for a customer
	 * @param minLevel
	 *            the minimum venue level
	 * @param maxLevel
	 *            the maximum venue level
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	@POST
	@Path("/hold")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SeatHold holdSeats(@QueryParam("numSeats") int numSeats, @QueryParam("minLevel") int minLevel,
			@QueryParam("maxLevel") int maxLevel, @QueryParam("customerEmail") String customerEmail) {
		Optional<Integer> minimumLevel = Optional.of(minLevel);
		Optional<Integer> maximumLevel = Optional.of(maxLevel);
		setOptionalLevel(minimumLevel, minLevel);
		setOptionalLevel(maximumLevel, maxLevel);
		return ticketService.findAndHoldSeats(numSeats, minimumLevel, maximumLevel, customerEmail);
	}

	/**
	 * Commit seats held for a specific customer
	 * 
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	@POST
	@Path("/reservation")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String reserveSeats(@QueryParam("seatHoldId") int seatHoldId,
			@QueryParam("customerEmail") String customerEmail) {
		return ticketService.reserveSeats(seatHoldId, customerEmail);
	}

	private void setOptionalLevel(Optional<Integer> optionalLevel, int intLevel) {
		if (intLevel == Constants.ZERO) {
			optionalLevel = Optional.empty();
		} else {
			optionalLevel = Optional.of(intLevel);
		}
	}
}
