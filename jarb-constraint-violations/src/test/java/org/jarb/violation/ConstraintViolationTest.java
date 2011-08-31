package org.jarb.violation;

import static org.jarb.violation.DatabaseConstraintViolation.violation;
import static org.jarb.violation.DatabaseConstraintViolationType.CHECK_FAILED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConstraintViolationTest {

    /**
     * Constraint violations should have a string representation that shows its attributes.
     */
    @Test
    public void testToString() {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(CHECK_FAILED);
        violationBuilder.named("ck_name_cannot_be_henk");
        DatabaseConstraintViolation violation = violationBuilder.build();
        final String toString = violation.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CHECK_FAILED"));
        assertTrue(toString.contains("ck_name_cannot_be_henk"));
    }

}
