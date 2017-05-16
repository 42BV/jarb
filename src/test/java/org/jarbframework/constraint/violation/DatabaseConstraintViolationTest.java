package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DatabaseConstraintViolationTest {

    @Test
    public void testToString() {
        DatabaseConstraintViolation violation = new DatabaseConstraintViolation(CHECK_FAILED, "ck_name_cannot_be_henk");
        
        final String violationAsString = violation.toString();
        assertNotNull(violationAsString);
        assertTrue(violationAsString.contains("CHECK_FAILED"));
        assertTrue(violationAsString.contains("ck_name_cannot_be_henk"));
    }

}
