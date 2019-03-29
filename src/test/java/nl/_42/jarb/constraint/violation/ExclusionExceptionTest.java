package nl._42.jarb.constraint.violation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExclusionExceptionTest {

    private final DatabaseConstraintViolation violation;

    public ExclusionExceptionTest() {
        violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.EXCLUSION).constraint("ex_gebruiker_gebruikersnaam_for_non_deleted_gebruikers").build();
    }

    @Test
    public void testDefaultMessage() {
        ExclusionException exception = new ExclusionException(violation);
        assertEquals(violation, exception.getViolation());
        assertEquals("Exclusion 'ex_gebruiker_gebruikersnaam_for_non_deleted_gebruikers' failed.", exception.getMessage());
    }

    @Test
    public void testCustomMessage() {
        ExclusionException exception = new ExclusionException(violation, "Custom message");
        assertEquals(violation, exception.getViolation());
        assertEquals("Custom message", exception.getMessage());
    }

}
