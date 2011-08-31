package org.jarb.violation;

import static org.jarb.violation.DatabaseConstraintViolation.violation;
import static org.jarb.violation.DatabaseConstraintViolationType.NOT_NULL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NotNullViolationExceptionTest {
    private final DatabaseConstraintViolation notNullConstraintViolation;

    public NotNullViolationExceptionTest() {
        notNullConstraintViolation = violation(NOT_NULL).setColumnName("column_name").build();
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

}
