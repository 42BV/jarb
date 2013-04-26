package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NotNullViolationExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public NotNullViolationExceptionTest() {
        violation = builder(NOT_NULL).column("column_name").build();
    }

    @Test
    public void testDefaultMessage() {
        NotNullViolationException exception = new NotNullViolationException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Column 'column_name' cannot be null.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        NotNullViolationException exception = new NotNullViolationException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
