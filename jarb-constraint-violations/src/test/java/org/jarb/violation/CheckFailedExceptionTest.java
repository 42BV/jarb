package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.CHECK_FAILED;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_VIOLATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CheckFailedExceptionTest {
    private final ConstraintViolation checkConstraintViolation;
    
    public CheckFailedExceptionTest() {
        checkConstraintViolation = new ConstraintViolation.Builder(CHECK_FAILED).setConstraintName("ck_person_birth_before_now").build();
    }

    @Test
    public void testDefaultMessage() {
        CheckFailedException exception = new CheckFailedException(checkConstraintViolation);
        assertEquals(checkConstraintViolation, exception.getViolation());
        assertEquals("Check 'ck_person_birth_before_now' failed.", exception.getMessage());
    }
    
    @Test
    public void testCustomMessage() {
        CheckFailedException exception = new CheckFailedException(checkConstraintViolation, "Custom message");
        assertEquals(checkConstraintViolation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }
    
    @Test
    public void testInvalidViolationType() {
        try {
            new CheckFailedException(new ConstraintViolation.Builder(UNIQUE_VIOLATION).build());
            fail("Invalid violation types should result in an illegal argument exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Check failed exceptions can only be used for check failed violations.", e.getMessage());
        }
    }
    
}
