package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class FieldPathTest {
    private FieldPath fieldPath;

    @Before
    public void setUp() throws SecurityException, NoSuchFieldException {
        fieldPath = FieldPath.startingFrom(Person.class, "address").to("street").to(Street.class.getDeclaredField("name"));
    }
    
    @Test
    public void testGetFieldValue() {
        Person person = new Person();
        Address address = new Address();
        Street street = new Street();
        street.name = "Teststreet 45";
        address.street = street;
        person.address = address;
        assertEquals("Teststreet 45", fieldPath.getValueFor(person));
    }
    
    @Test
    public void testInvalidPathByString() {
        try {
            // Address has no 'unknown' field
            FieldPath.startingFrom(Person.class, "address").to("unknown").to("name");
            fail("Invalid paths should not be accepted during construction.");
        } catch(IllegalStateException e) {
            assertEquals("Field 'unknown' does not exist in 'Address'.", e.getMessage());
        }
    }
    
    @Test
    public void testInvalidPathByField() throws SecurityException, NoSuchFieldException {
        try {
            // Field 'name' has been declared in Street, not Address
            FieldPath.startingFrom(Person.class, "address").to(Street.class.getDeclaredField("name"));
            fail("Invalid paths should not be accepted during construction.");
        } catch(IllegalStateException e) {
            assertEquals("Cannot extend path to 'Street.name' as the field is not declared in 'Address'.", e.getMessage());
        }
    }
    
    public static class Person {
        @SuppressWarnings("unused")
        private Address address;
    }
    
    public static class Address {
        @SuppressWarnings("unused")
        private Street street;
    }
    
    public static class Street {
        @SuppressWarnings("unused")
        private String name;
    }
    
}
