package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.CANNOT_BE_NULL;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_VIOLATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class UniqueKeyViolationExceptionTest {
    private final ConstraintViolation uniqueConstraintViolation;
    
    public UniqueKeyViolationExceptionTest() {
        uniqueConstraintViolation = new ConstraintViolation.Builder(UNIQUE_VIOLATION).setConstraintName("uk_persons_name").build();
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
    
    @Test
    public void testInvalidViolationType() {
        try {
            new UniqueKeyViolationException(new ConstraintViolation.Builder(CANNOT_BE_NULL).build());
            fail("Invalid violation types should result in an illegal argument exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Unique key violation exceptions can only be used for unique key violations.", e.getMessage());
        }
    }
    
}
