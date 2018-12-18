package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintViolation.builder;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InvalidTypeExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public InvalidTypeExceptionTest() {
        violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.INVALID_TYPE).build();
    }

    @Test
    public void testDefaultMessage() {
        InvalidTypeException exception = new InvalidTypeException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Column is of an invalid type.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        InvalidTypeException exception = new InvalidTypeException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
