package org.jarb.violation.factory;

import static org.jarb.violation.ConstraintViolation.createViolation;
import static org.jarb.violation.ConstraintViolationType.CHECK_FAILED;
import static org.jarb.violation.ConstraintViolationType.FOREIGN_KEY;
import static org.jarb.violation.ConstraintViolationType.INVALID_TYPE;
import static org.jarb.violation.ConstraintViolationType.LENGTH_EXCEEDED;
import static org.jarb.violation.ConstraintViolationType.NOT_NULL;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarb.violation.CheckFailedException;
import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationException;
import org.jarb.violation.ForeignKeyViolationException;
import org.jarb.violation.InvalidTypeException;
import org.jarb.violation.LengthExceededException;
import org.jarb.violation.NotNullViolationException;
import org.jarb.violation.UniqueKeyViolationException;
import org.junit.Before;
import org.junit.Test;

public class SimpleConstraintViolationExceptionFactoryTest {
    private SimpleConstraintViolationExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new SimpleConstraintViolationExceptionFactory();
    }

    @Test
    public void testCheckFailed() {
        ConstraintViolation violation = createViolation(CHECK_FAILED).setConstraintName("ck_name_cannot_be_henk").build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        ConstraintViolation violation = createViolation(UNIQUE_KEY).setConstraintName("uk_persons_name").build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testForeignKeyViolated() {
        ConstraintViolation violation = createViolation(FOREIGN_KEY).setConstraintName("fk_persons_parent").build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof ForeignKeyViolationException);
        assertEquals("Foreign key 'fk_persons_parent' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        ConstraintViolation violation = createViolation(NOT_NULL).setColumnName("name").build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        ConstraintViolation violation = createViolation(INVALID_TYPE).build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        ConstraintViolation violation = createViolation(LENGTH_EXCEEDED).build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
