package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintViolation.builder;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CheckFailedExceptionTest {
    
    private final DatabaseConstraintViolation violation;

    public CheckFailedExceptionTest() {
        violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.CHECK_FAILED).constraint("ck_person_birth_before_now").build();
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
