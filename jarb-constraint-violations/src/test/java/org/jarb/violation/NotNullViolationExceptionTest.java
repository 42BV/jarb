package org.jarb.violation;

import static org.jarb.violation.ConstraintViolationType.CANNOT_BE_NULL;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_VIOLATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class NotNullViolationExceptionTest {
    private final ConstraintViolation notNullConstraintViolation;
    
    public NotNullViolationExceptionTest() {
        notNullConstraintViolation = new ConstraintViolation.Builder(CANNOT_BE_NULL).setColumnName("column_name").build();
    }

    @Test
    public void testDefaultMessage() {
        NotNullViolationException exception = new NotNullViolationException(notNullConstraintViolation);
        assertEquals(notNullConstraintViolation, exception.getViolation());
        assertEquals("Column 'column_name' cannot be null.", exception.getMessage());
    }
    
    @Test
    public void testCustomMessage() {
        NotNullViolationException exception = new NotNullViolationException(notNullConstraintViolation, "Custom message");
        assertEquals(notNullConstraintViolation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }
    
    @Test
    public void testInvalidViolationType() {
        try {
            new NotNullViolationException(new ConstraintViolation.Builder(UNIQUE_VIOLATION).build());
            fail("Invalid violation types should result in an illegal argument exception");
        } catch(IllegalArgumentException e) {
            assertEquals("Not null violation exceptions can only be used for not null violations.", e.getMessage());
        }
    }
    
}
