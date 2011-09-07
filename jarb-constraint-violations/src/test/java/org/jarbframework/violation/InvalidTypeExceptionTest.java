package org.jarbframework.violation;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.INVALID_TYPE;
import static org.junit.Assert.assertEquals;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.InvalidTypeException;
import org.junit.Test;

public class InvalidTypeExceptionTest {
    private final DatabaseConstraintViolation typeConstraintViolation;

    public InvalidTypeExceptionTest() {
        typeConstraintViolation = violation(INVALID_TYPE).build();
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
