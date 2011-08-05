package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.LENGTH_EXCEEDED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LengthExceededExceptionTest {
    private final ConstraintViolation lengthConstraintViolation;

    public LengthExceededExceptionTest() {
        lengthConstraintViolation = new ConstraintViolation.Builder(LENGTH_EXCEEDED).build();
    }

    @Test
    public void testDefaultMessage() {
        LengthExceededException exception = new LengthExceededException(lengthConstraintViolation);
        assertEquals(lengthConstraintViolation, exception.getViolation());
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        LengthExceededException exception = new LengthExceededException(lengthConstraintViolation, "Custom message");
        assertEquals(lengthConstraintViolation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
