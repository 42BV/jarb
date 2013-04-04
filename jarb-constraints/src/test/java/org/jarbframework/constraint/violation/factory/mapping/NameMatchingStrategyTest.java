package org.jarbframework.constraint.violation.factory.mapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.violation.factory.mapping.NameMatchingStrategy;
import org.junit.Test;

public class NameMatchingStrategyTest {

    @Test
    public void testEquals() {
        assertTrue(NameMatchingStrategy.EXACT.matches("uk_persons_name", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.EXACT.matches("uk_persons_name", "uk_cars_license"));
    }
    
    @Test
    public void testEqualsIgnoreCase() {
        assertTrue(NameMatchingStrategy.EXACT_IGNORE_CASE.matches("Uk_Persons_Name", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.EXACT_IGNORE_CASE.matches("Uk_Persons_Name", "uk_cars_license"));
    }
    
    @Test
    public void testStartsWith() {
        assertTrue(NameMatchingStrategy.STARTS_WITH.matches("uk", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.STARTS_WITH.matches("uk", "pk_persons_id"));
    }
    
    @Test
    public void testEndsWith() {
        assertTrue(NameMatchingStrategy.ENDS_WITH.matches("name", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.ENDS_WITH.matches("name", "pk_persons_id"));
    }
    
    @Test
    public void testContains() {
        assertTrue(NameMatchingStrategy.CONTAINS.matches("persons", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.CONTAINS.matches("persons", "uk_cars_license"));
    }
    
    @Test
    public void testRegex() {
        assertTrue(NameMatchingStrategy.REGEX.matches("\\w+_persons_\\w+", "uk_persons_name"));
        assertFalse(NameMatchingStrategy.REGEX.matches("\\w+_persons_\\w+", "uk_cars_license"));
    }
    
}
