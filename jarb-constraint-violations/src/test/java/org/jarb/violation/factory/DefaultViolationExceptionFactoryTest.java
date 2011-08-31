package org.jarb.violation.factory;

import static org.jarb.violation.DatabaseConstraintViolation.violation;
import static org.jarb.violation.DatabaseConstraintViolationType.CHECK_FAILED;
import static org.jarb.violation.DatabaseConstraintViolationType.FOREIGN_KEY;
import static org.jarb.violation.DatabaseConstraintViolationType.INVALID_TYPE;
import static org.jarb.violation.DatabaseConstraintViolationType.LENGTH_EXCEEDED;
import static org.jarb.violation.DatabaseConstraintViolationType.NOT_NULL;
import static org.jarb.violation.DatabaseConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarb.violation.CheckFailedException;
import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.DatabaseConstraintViolationException;
import org.jarb.violation.ForeignKeyViolationException;
import org.jarb.violation.InvalidTypeException;
import org.jarb.violation.LengthExceededException;
import org.jarb.violation.NotNullViolationException;
import org.jarb.violation.UniqueKeyViolationException;
import org.junit.Before;
import org.junit.Test;

public class DefaultViolationExceptionFactoryTest {
    private DefaultViolationExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new DefaultViolationExceptionFactory();
    }

    @Test
    public void testCheckFailed() {
        DatabaseConstraintViolation violation = violation(CHECK_FAILED).setConstraintName("ck_name_cannot_be_henk").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        DatabaseConstraintViolation violation = violation(UNIQUE_KEY).setConstraintName("uk_persons_name").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testForeignKeyViolated() {
        DatabaseConstraintViolation violation = violation(FOREIGN_KEY).setConstraintName("fk_persons_parent").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof ForeignKeyViolationException);
        assertEquals("Foreign key 'fk_persons_parent' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        DatabaseConstraintViolation violation = violation(NOT_NULL).setColumnName("name").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        DatabaseConstraintViolation violation = violation(INVALID_TYPE).build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        DatabaseConstraintViolation violation = violation(LENGTH_EXCEEDED).build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
