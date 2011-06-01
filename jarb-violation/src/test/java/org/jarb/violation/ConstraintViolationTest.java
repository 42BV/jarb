package org.jarb.violation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstraintViolationTest {

    /**
     * Constraint violations should have a string representation that shows its attributes.
     */
    @Test
    public void testToString() {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.CHECK_FAILED);
        violation.setConstraintName("ck_name_cannot_be_henk");
        final String toString = violation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CHECK_FAILED"));
        assertTrue(toString.contains("ck_name_cannot_be_henk"));
    }

}
