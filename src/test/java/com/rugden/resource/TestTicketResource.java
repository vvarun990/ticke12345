package com.rugden.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.test.framework.JerseyTest;

public class TestTicketResource extends JerseyTest {

	public TestTicketResource() {
		super("com.rugden.resource");
	}

	@Test
	public void shouldInitializeTicketServiceDependencies() {
		TicketResource resource = new TicketResource();
		Assert.assertNotNull("Bookings should not be null", resource.getBookings());
		Assert.assertNotNull("Reservations should not be null", resource.getReservations());
		Assert.assertNotNull("TicketService should not be null", resource.getTicketService());
	}

	@Test
	public void shouldReturnAllAvailableTicketsInVenue() {
		WebResource webResource = resource();
		String responseMsg = webResource.path("tickets").get(String.class);
		Assert.assertEquals("6250", responseMsg);
	}

	@Test
	public void shouldReturnAllAvailableTicketsInVenueLevel() {
		WebResource webResource = resource();
		String responseMsg = webResource.path("tickets/1").get(String.class);
		Assert.assertEquals("1250", responseMsg);
	}

	@Test
	public void shouldReturnErrorMessageWithInValidQueryParam() {
		WebResource webResource = resource();
		String responseMsg = webResource.path("tickets/5").get(String.class);
		Assert.assertEquals("Invalid level", responseMsg);
	}

	@Test
	public void shouldReturnSeatHoldObjectOnSeatHold() {
		MultivaluedMap<String, String> form = new MultivaluedMapImpl();
		form.add("numSeats", "400");
		form.add("minLevel", "2");
		form.add("maxLevel", "2");
		form.add("customerEmail", "rugden@gmail.com");

		WebResource webResource = resource();
		String responseMsg = webResource.path("tickets/hold").queryParams(form).type(MediaType.APPLICATION_JSON_TYPE)
				.post(String.class);
		JsonObject jobj = (JsonObject) new JsonParser().parse(responseMsg);
		String email = jobj.get("email").getAsString();
		JsonObject holdsPerLevel = (JsonObject) jobj.get("holdsPerLevel");
		JsonObject entry = (JsonObject) holdsPerLevel.get("entry");
		String numSeats = entry.get("value").getAsString();
		Assert.assertEquals("rugden@gmail.com", email);
		Assert.assertEquals("400", numSeats);
	}

	@Test
	public void shouldReturnMessageOnUnAvailableSeatReservations() {
		MultivaluedMap<String, String> form = new MultivaluedMapImpl();
		form.add("seatHoldId", "1");
		form.add("customerEmail", "reston@gmail.com");

		WebResource webResource = resource();
		String confirmationCode = webResource.path("tickets/reservation").queryParams(form).accept(MediaType.TEXT_PLAIN)
				.type(MediaType.TEXT_PLAIN).post(String.class);
		Assert.assertEquals("NoReservation", confirmationCode);
	}

}
