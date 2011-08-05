package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UniqueKeyViolationExceptionTest {
    private final ConstraintViolation uniqueConstraintViolation;

    public UniqueKeyViolationExceptionTest() {
        uniqueConstraintViolation = new ConstraintViolation.Builder(UNIQUE_KEY).setConstraintName("uk_persons_name").build();
    }

    @Test
    public void testDefaultMessage() {
        UniqueKeyViolationException exception = new UniqueKeyViolationException(uniqueConstraintViolation);
        assertEquals(uniqueConstraintViolation, exception.getViolation());
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        UniqueKeyViolationException exception = new UniqueKeyViolationException(uniqueConstraintViolation, "Custom message");
        assertEquals(uniqueConstraintViolation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
