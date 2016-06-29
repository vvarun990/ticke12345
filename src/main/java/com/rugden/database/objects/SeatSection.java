package com.rugden.database.objects;

import com.rugden.constants.Constants;

/**
 * Object representing a level of the performance venue
 * @author Rug Den
 *
 */
public class SeatSection {
	private String levelName;
	private Long seatPrice = 0L;
	private Integer totalSeats = 0;
	private Integer totalRows = 0;
	private Integer seatsPerRow = 0;
	private Integer availableSeats = 0;
	private Integer heldSeats = 0;
	private Integer reservedSeats = 0;

	public SeatSection() {
	}

	public void initialize() {
		availableSeats = getTotalSeats() - (heldSeats + reservedSeats);
	}

	public synchronized String getLevelName() {
		return levelName;
	}

	public synchronized void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public synchronized Long getSeatPrice() {
		return seatPrice;
	}

	public synchronized void setSeatPrice(Long seatPrice) {
		this.seatPrice = seatPrice;
	}

	public synchronized Integer getTotalSeats() {
		totalSeats = seatsPerRow * totalRows;
		return totalSeats;
	}

	public synchronized void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public synchronized Integer getTotalRows() {
		return totalRows;
	}

	public synchronized void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public synchronized Integer getSeatsPerRow() {
		return seatsPerRow;
	}

	public synchronized void setSeatsPerRow(Integer seatsPerRow) {
		this.seatsPerRow = seatsPerRow;
	}

	public synchronized Integer getAvailableSeats() {
		return availableSeats;
	}

	public synchronized Integer getHeldSeats() {
		return heldSeats;
	}

	public synchronized Integer getReservedSeats() {
		return reservedSeats;
	}

	public synchronized void setHeldSeats(Integer heldSeats) {
		this.heldSeats = heldSeats;
	}

	public synchronized void setReservedSeats(Integer reservedSeats) {
		this.reservedSeats = reservedSeats;
	}

	public synchronized void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}

	/**
	 * Hold given number of seats for a customer
	 * @param seatsToHold seats to hold
	 * @return Integer value. <br/>0 when availableSeats >= seatsToHold<br/>
	 * negative difference when availableSeats< seatsToHold
	 */
	public synchronized Integer holdSeats(Integer seatsToHold) {
		Integer holdStatus = Constants.ZERO;
		if (availableSeats >= seatsToHold) {
			setAvailableSeats(availableSeats - seatsToHold);
			setHeldSeats(heldSeats + seatsToHold);
		} else {
			holdStatus = availableSeats - seatsToHold;
			setHeldSeats(heldSeats + availableSeats);
			setAvailableSeats(Constants.ZERO);
		}
		return holdStatus;
	}

	/**
	 * remove seat holds from venue level
	 * @param holdsToRemove holds to remove
	 */
	public synchronized void removeSeatsHold(Integer holdsToRemove) {
		setAvailableSeats(availableSeats + holdsToRemove);
		setHeldSeats(heldSeats - holdsToRemove);
	}

	/**
	 * reserve seats at venue level
	 * @param seatsToReserve seats to reserve
	 */
	public synchronized void reserveSeats(Integer seatsToReserve) {
		setHeldSeats(heldSeats - seatsToReserve);
		setReservedSeats(reservedSeats + seatsToReserve);
	}

}
