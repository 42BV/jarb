package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.INVALID_TYPE;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_VIOLATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class InvalidTypeExceptionTest {
    private final ConstraintViolation typeConstraintViolation;
    
    public InvalidTypeExceptionTest() {
        typeConstraintViolation = new ConstraintViolation.Builder(INVALID_TYPE).build();
    }

    @Test
    public void testDefaultMessage() {
        InvalidTypeException exception = new InvalidTypeException(typeConstraintViolation);
        assertEquals(typeConstraintViolation, exception.getViolation());
        assertEquals("Column is of an invalid type.", exception.getMessage());
    }
    
    @Test
    public void testCustomMessage() {
        InvalidTypeException exception = new InvalidTypeException(typeConstraintViolation, "Custom message");
        assertEquals(typeConstraintViolation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }
    
    @Test
    public void testInvalidViolationType() {
        try {
            new InvalidTypeException(new ConstraintViolation.Builder(UNIQUE_VIOLATION).build());
            fail("Invalid violation types should result in an illegal argument exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Invalid type exceptions can only be used for invalid type violations.", e.getMessage());
        }
    }
    
}
