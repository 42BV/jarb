package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.SpecialCustomer;

public class SpecialCustomerTest {

    private SpecialCustomer specialCustomer;

    @Before
    public void setUpCustomer() {
        specialCustomer = new SpecialCustomer();
    }

    @Test
    public void testSetGetCompanyLocation() {
        final String location = "test";
        specialCustomer.setLocation(location);
        assertEquals(location, specialCustomer.getLocation());
    }
}
