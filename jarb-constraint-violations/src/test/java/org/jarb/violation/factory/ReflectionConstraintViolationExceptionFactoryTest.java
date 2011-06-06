package org.jarb.violation.factory;

import static org.junit.Assert.assertTrue;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.ConstraintViolationType;
import org.jarb.violation.domain.LicenseNumberAlreadyExistsException;
import org.junit.Test;

public class ReflectionConstraintViolationExceptionFactoryTest {

    /**
     * Custom exception classes can be used if they have a constraint violation constructor.
     */
    @Test
    public void testInstantiateWithViolation() {
        ConstraintViolationExceptionFactory factory = new ReflectionConstraintViolationExceptionFactory(LicenseNumberAlreadyExistsException.class);
        ConstraintViolation violation = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE_VIOLATION).build();
        Throwable exception = factory.createException(violation);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }

    /**
     * Or a nullary constructor.
     */
    @Test
    public void testInstantiateNullary() {
        ConstraintViolationExceptionFactory factory = new ReflectionConstraintViolationExceptionFactory(IllegalArgumentException.class);
        ConstraintViolation violation = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE_VIOLATION).build();
        Throwable exception = factory.createException(violation);
        assertTrue(exception instanceof IllegalArgumentException);
    }

    /**
     * However, if it has no constraint violation, or nullary, constructor, an illegal state exception is thrown.
     */
    @Test(expected = IllegalStateException.class)
    public void testNoMatchingConstructor() {
        ConstraintViolationExceptionFactory factory = new ReflectionConstraintViolationExceptionFactory(ExceptionWithUnsupportedConstructor.class);
        ConstraintViolation violation = new ConstraintViolation.Builder(ConstraintViolationType.UNIQUE_VIOLATION).build();
        factory.createException(violation);
    }

    // Exception with a non-default constructor, used only for testing
    public static class ExceptionWithUnsupportedConstructor extends RuntimeException {
        private static final long serialVersionUID = -8084276175092538738L;

        public ExceptionWithUnsupportedConstructor(int[] arg, String msg) {
            super(msg);
        }
    }

}
