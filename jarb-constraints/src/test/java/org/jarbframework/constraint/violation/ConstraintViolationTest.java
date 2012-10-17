package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.junit.Test;

public class ConstraintViolationTest {

    @Test
    public void testToString() {
        DatabaseConstraintViolation violation = violaton(CHECK_FAILED).constraint("ck_name_cannot_be_henk").build();
        final String toString = violation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CHECK_FAILED"));
        assertTrue(toString.contains("ck_name_cannot_be_henk"));
    }

}
