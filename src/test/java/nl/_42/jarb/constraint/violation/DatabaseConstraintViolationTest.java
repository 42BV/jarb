package nl._42.jarb.constraint.violation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseConstraintViolationTest {

    @Test
    public void testToString() {
        DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.CHECK_FAILED, "ck_name_cannot_be_henk");
        
        final String violationAsString = violation.toString();
        assertNotNull(violationAsString);
        assertTrue(violationAsString.contains("CHECK_FAILED"));
        assertTrue(violationAsString.contains("ck_name_cannot_be_henk"));
    }

}
