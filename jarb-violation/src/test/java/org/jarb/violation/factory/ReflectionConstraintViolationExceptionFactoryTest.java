package org.jarb.violation.factory;

import static org.junit.Assert.assertTrue;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.domain.LicenseNumberAlreadyExistsException;
import org.junit.Test;

public class ReflectionConstraintViolationExceptionFactoryTest {

    /**
     * Tests that a custom exception can be instantiated, whenever it has a constraint violation constructor.
     */
    @Test
    public void testInstantiateException() {
        ConstraintViolationExceptionFactory factory = ReflectionConstraintViolationExceptionFactory.forClass(LicenseNumberAlreadyExistsException.class);
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.UNIQUE_VIOLATION);
        Throwable exception = factory.createException(violation);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }

    /**
     * However, if it has no constraint violation constructor, a runtime exception is thrown.
     */
    @Test(expected = RuntimeException.class)
    public void testNoMatchingConstructor() {
        ConstraintViolationExceptionFactory factory = ReflectionConstraintViolationExceptionFactory.forClass(IllegalArgumentException.class);
        ConstraintViolation violation = new ConstraintViolation(ConstraintViolationType.UNIQUE_VIOLATION);
        factory.createException(violation);
    }

}
