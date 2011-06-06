package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;

public class AddressTest {

    private Address address;

    @Before
    public void setupAddressTest() {
        address = new Address();
    }

    @Test
    public void testSetGetAddress() {
        address.setStreetAndNumber("Schoolstraat 12");
        assertEquals("Schoolstraat 12", address.getStreetAndNumber());
    }

    @Test
    public void testSetGetCity() {
        address.setCity("Zoetermeer");
        assertEquals("Zoetermeer", address.getCity());
    }

    @Test
    public void testConstructorWithArguments() {
        Address address2 = new Address("Damrak 4", "Amsterdam");
        assertEquals("Damrak 4", address2.getStreetAndNumber());
        assertEquals("Amsterdam", address2.getCity());
    }

}
