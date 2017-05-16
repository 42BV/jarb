package org.jarbframework.constraint.violation.factory;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.builder;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.violation.CheckFailedException;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.DatabaseConstraintViolationException;
import org.jarbframework.constraint.violation.ForeignKeyViolationException;
import org.jarbframework.constraint.violation.InvalidTypeException;
import org.jarbframework.constraint.violation.LengthExceededException;
import org.jarbframework.constraint.violation.NotNullViolationException;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.junit.Before;
import org.junit.Test;

public class DefaultConstraintExceptionFactoryTest {
    
    private DefaultConstraintExceptionFactory exceptionFactory;

    @Before
    public void setUp() {
        exceptionFactory = new DefaultConstraintExceptionFactory();
    }

    @Test
    public void testCheckFailed() {
        DatabaseConstraintViolation violation = builder(CHECK_FAILED).constraint("ck_name_cannot_be_henk").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        DatabaseConstraintViolation violation = builder(UNIQUE_KEY).constraint("uk_persons_name").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testForeignKeyViolated() {
        DatabaseConstraintViolation violation = builder(FOREIGN_KEY).constraint("fk_persons_parent").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof ForeignKeyViolationException);
        assertEquals("Foreign key 'fk_persons_parent' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        DatabaseConstraintViolation violation = builder(NOT_NULL).column("name").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        DatabaseConstraintViolation violation = builder(INVALID_TYPE).build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        DatabaseConstraintViolation violation = builder(LENGTH_EXCEEDED).build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
