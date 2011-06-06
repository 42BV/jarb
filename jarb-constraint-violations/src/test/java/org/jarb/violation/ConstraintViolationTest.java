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
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.CHECK_FAILED);
        violationBuilder.setConstraintName("ck_name_cannot_be_henk");
        ConstraintViolation violation = violationBuilder.build();
        final String toString = violation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CHECK_FAILED"));
        assertTrue(toString.contains("ck_name_cannot_be_henk"));
    }

}
