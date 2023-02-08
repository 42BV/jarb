package nl._42.jarb.constraint.violation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LengthExceededExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public LengthExceededExceptionTest() {
        violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.LENGTH_EXCEEDED).build();
    }

    @Test
    public void testDefaultMessage() {
        LengthExceededException exception = new LengthExceededException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        LengthExceededException exception = new LengthExceededException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
