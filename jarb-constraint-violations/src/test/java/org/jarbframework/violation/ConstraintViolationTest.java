package org.jarbframework.violation;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.CHECK_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstraintViolationTest {

    @Test
    public void testToString() {
        DatabaseConstraintViolation violation = violation(CHECK_FAILED).constraint("ck_name_cannot_be_henk").build();
        final String toString = violation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CHECK_FAILED"));
        assertTrue(toString.contains("ck_name_cannot_be_henk"));
    }

}
