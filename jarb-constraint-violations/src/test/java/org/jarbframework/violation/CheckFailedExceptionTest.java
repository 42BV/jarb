package org.jarbframework.violation;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.CHECK_FAILED;
import static org.junit.Assert.assertEquals;

import org.jarbframework.violation.CheckFailedException;
import org.jarbframework.violation.DatabaseConstraintViolation;
import org.junit.Test;

public class CheckFailedExceptionTest {
    private final DatabaseConstraintViolation checkConstraintViolation;

    public CheckFailedExceptionTest() {
        checkConstraintViolation = violation(CHECK_FAILED).named("ck_person_birth_before_now").build();
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

}
