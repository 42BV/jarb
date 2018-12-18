package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintViolation.builder;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UniqueKeyViolationExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public UniqueKeyViolationExceptionTest() {
        violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.UNIQUE_KEY).constraint("uk_persons_name").build();
    }

    @Test
    public void testDefaultMessage() {
        UniqueKeyViolationException exception = new UniqueKeyViolationException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        UniqueKeyViolationException exception = new UniqueKeyViolationException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
