package nl._42.jarb.constraint.violation;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
