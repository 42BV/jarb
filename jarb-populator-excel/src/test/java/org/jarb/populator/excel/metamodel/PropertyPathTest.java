package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class PropertyPathTest {
    private PropertyPath fieldPath;

    @Before
    public void setUp() throws SecurityException, NoSuchFieldException {
        fieldPath = PropertyPath.startingFrom(Person.class, "address").to("street").to("name");
    }
    
    @Test
    public void testGetFieldValue() {
        Person person = new Person();
        Address address = new Address();
        Street street = new Street();
        street.name = "Teststreet 45";
        address.street = street;
        person.address = address;
        assertEquals("Teststreet 45", fieldPath.traverse(person));
    }
    
    @Test
    public void testInvalidPathByString() {
        try {
            // Address has no 'unknown' field
            PropertyPath.startingFrom(Person.class, "address").to("unknown").to("name");
            fail("Invalid paths should not be accepted during construction.");
        } catch(IllegalStateException e) {
            assertEquals("Property 'unknown' does not exist in 'Address'.", e.getMessage());
        }
    }
    
    @Test
    public void testEquals() {
        // Reference to the same object should always be equal
        assertTrue(fieldPath.equals(fieldPath));
        // Similar path, just a different instance
        assertTrue(fieldPath.equals(PropertyPath.startingFrom(Person.class, "address").to("street").to("name")));
        // Different path, should not be equal
        assertFalse(fieldPath.equals(PropertyPath.startingFrom(Person.class, "address").to("street")));
    }
    
    @Test
    public void testHashCode() {
        // Reference to the same object should always have equal hash code
        assertEquals(fieldPath.hashCode(), fieldPath.hashCode());
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
