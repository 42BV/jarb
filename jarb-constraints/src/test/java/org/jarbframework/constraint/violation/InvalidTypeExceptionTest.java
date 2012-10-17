package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InvalidTypeExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public InvalidTypeExceptionTest() {
        violation = violaton(INVALID_TYPE).build();
    }

    @Test
    public void testDefaultMessage() {
        InvalidTypeException exception = new InvalidTypeException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Column is of an invalid type.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        InvalidTypeException exception = new InvalidTypeException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
