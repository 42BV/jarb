package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.LENGTH_EXCEEDED;
import static org.jarb.violation.ConstraintViolationType.UNIQUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    
    @Test
    public void testInvalidViolationType() {
        try {
            new LengthExceededException(new ConstraintViolation.Builder(UNIQUE).build());
            fail("Invalid violation types should result in an illegal argument exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Length exceeded exceptions can only be used for length exceeded violations.", e.getMessage());
        }
    }
    
}
