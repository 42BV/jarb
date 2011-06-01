package org.jarb.violation.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jarb.violation.CheckFailedException;
import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationException;
import org.jarb.violation.ConstraintViolationType;
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
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.CHECK_FAILED);
        violation.setConstraintName("ck_name_cannot_be_henk");
        ConstraintViolationException exception = factory.createException(violation);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.UNIQUE_VIOLATION);
        violation.setConstraintName("uk_persons_name");
        ConstraintViolationException exception = factory.createException(violation);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.CANNOT_BE_NULL);
        violation.setColumnName("name");
        ConstraintViolationException exception = factory.createException(violation);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.INVALID_TYPE);
        ConstraintViolationException exception = factory.createException(violation);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.LENGTH_EXCEEDED);
        ConstraintViolationException exception = factory.createException(violation);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
