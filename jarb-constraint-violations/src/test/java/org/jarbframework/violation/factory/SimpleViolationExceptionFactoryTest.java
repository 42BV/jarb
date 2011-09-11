package org.jarbframework.violation.factory;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.CHECK_FAILED;
import static org.jarbframework.violation.DatabaseConstraintViolationType.FOREIGN_KEY;
import static org.jarbframework.violation.DatabaseConstraintViolationType.INVALID_TYPE;
import static org.jarbframework.violation.DatabaseConstraintViolationType.LENGTH_EXCEEDED;
import static org.jarbframework.violation.DatabaseConstraintViolationType.NOT_NULL;
import static org.jarbframework.violation.DatabaseConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarbframework.violation.CheckFailedException;
import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationException;
import org.jarbframework.violation.ForeignKeyViolationException;
import org.jarbframework.violation.InvalidTypeException;
import org.jarbframework.violation.LengthExceededException;
import org.jarbframework.violation.NotNullViolationException;
import org.jarbframework.violation.UniqueKeyViolationException;
import org.jarbframework.violation.factory.SimpleConstraintExceptionFactory;
import org.junit.Before;
import org.junit.Test;

public class SimpleViolationExceptionFactoryTest {
    private SimpleConstraintExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new SimpleConstraintExceptionFactory();
    }

    @Test
    public void testCheckFailed() {
        DatabaseConstraintViolation violation = violation(CHECK_FAILED).named("ck_name_cannot_be_henk").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        DatabaseConstraintViolation violation = violation(UNIQUE_KEY).named("uk_persons_name").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testForeignKeyViolated() {
        DatabaseConstraintViolation violation = violation(FOREIGN_KEY).named("fk_persons_parent").build();
        DatabaseConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof ForeignKeyViolationException);
        assertEquals("Foreign key 'fk_persons_parent' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        DatabaseConstraintViolation violation = violation(NOT_NULL).column("name").build();
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
