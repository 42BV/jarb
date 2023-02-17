package nl._42.jarb.domain;

import nl._42.jarb.constraint.validation.DatabaseConstrained;

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
