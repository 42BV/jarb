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
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.CHECK_FAILED);
        violationBuilder.setConstraintName("ck_name_cannot_be_henk");
        ConstraintViolation violation = violationBuilder.build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE_VIOLATION);
        violationBuilder.setConstraintName("uk_persons_name");
        ConstraintViolation violation = violationBuilder.build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.CANNOT_BE_NULL);
        violationBuilder.setColumnName("name");
        ConstraintViolation violation = violationBuilder.build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.INVALID_TYPE);
        ConstraintViolation violation = violationBuilder.build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        ConstraintViolation.Builder violationBuilder = new ConstraintViolation.Builder(ConstraintViolationType.LENGTH_EXCEEDED);
        ConstraintViolation violation = violationBuilder.build();
        ConstraintViolationException exception = factory.createException(violation, null);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
