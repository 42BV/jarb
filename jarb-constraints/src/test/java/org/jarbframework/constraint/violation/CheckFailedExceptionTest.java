package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CheckFailedExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public CheckFailedExceptionTest() {
        violation = builder(CHECK_FAILED).constraint("ck_person_birth_before_now").build();
    }

    @Test
    public void testDefaultMessage() {
        CheckFailedException exception = new CheckFailedException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Check 'ck_person_birth_before_now' failed.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        CheckFailedException exception = new CheckFailedException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
