package org.jarbframework.violation;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.LENGTH_EXCEEDED;
import static org.junit.Assert.assertEquals;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.LengthExceededException;
import org.junit.Test;

public class LengthExceededExceptionTest {
    private final DatabaseConstraintViolation violation;

    public LengthExceededExceptionTest() {
        violation = violation(LENGTH_EXCEEDED).build();
    }

    @Test
    public void testDefaultMessage() {
        LengthExceededException exception = new LengthExceededException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        LengthExceededException exception = new LengthExceededException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
