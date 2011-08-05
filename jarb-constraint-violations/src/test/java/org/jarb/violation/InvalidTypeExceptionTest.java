package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.INVALID_TYPE;
import static org.junit.Assert.assertEquals;

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

}
