package org.jarbframework.violation;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.NOT_NULL;
import static org.junit.Assert.assertEquals;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.NotNullViolationException;
import org.junit.Test;

public class NotNullViolationExceptionTest {
    private final DatabaseConstraintViolation notNullConstraintViolation;

    public NotNullViolationExceptionTest() {
        notNullConstraintViolation = violation(NOT_NULL).column("column_name").build();
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
