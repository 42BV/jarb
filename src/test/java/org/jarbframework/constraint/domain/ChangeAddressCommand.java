package org.jarbframework.constraint.domain;

import org.jarbframework.constraint.validation.DatabaseConstrained;

public class ChangeAddressCommand {

	@DatabaseConstrained(entityClass = Person.class, propertyName = "contact.address")
	private Address address;
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
