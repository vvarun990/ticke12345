package com.rugden.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * SeatHold object identifying the specific seats held by a cutomer and related
 * information<br/>
 * <li> seatHoldId    : the seat hold identifier
 * <li> price         : Total price of all held tickets
 * <li> email         : Unique identifier of customer that owns the hold
 * <li> holdsPerLevel : Numbers of seats held by customer at each venue level
 * 
 * @author Rug Den
 *
 */
@XmlRootElement
public class SeatHold {
	private Integer seatHoldId;
	private Long price;
	private String email;
	private Map<Integer, Integer> holdsPerLevel = new HashMap<Integer, Integer>();

	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<Integer, Integer> getHoldsPerLevel() {
		return holdsPerLevel;
	}

	public void setHoldsPerLevel(Map<Integer, Integer> holdsPerLevel) {
		this.holdsPerLevel = holdsPerLevel;
	}

}
