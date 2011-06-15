package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class FieldPathTest {
    private FieldPath fieldPath;

    @Before
    public void setUp() {
        fieldPath = FieldPath.forNames(Person.class, "address", "street", "name");
    }
    
    @Test
    public void testGetFieldValue() {
        Person person = new Person();
        Address address = new Address();
        Street street = new Street();
        street.name = "Teststreet 45";
        address.street = street;
        person.address = address;
        assertEquals("Teststreet 45", fieldPath.getFieldValue(person));
    }
    
    @Test
    public void testInvalidPath() {
        try {
            FieldPath.forNames(Person.class, "address", "unknown", "name");
            fail("Invalid paths should not be accepted during construction.");
        } catch(IllegalStateException e) {
            assertEquals("Field 'unknown' does not exist in 'Address'.", e.getMessage());
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
