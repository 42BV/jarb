package org.jarbframework.constraint.violation.factory;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.domain.UsernameAlreadyExistsException;
import org.junit.Test;

public class ReflectionConstraintExceptionFactoryTest {

    /**
     * Custom exception classes should be initialized using the best available constructor.
     * The best constructor has the most supported parameter types.
     */
    @Test
    public void testInstantiateWithBestConstructor() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(UsernameAlreadyExistsException.class);
        DatabaseConstraintViolation violation = violaton(UNIQUE_KEY).constraint("uk_cars_license_number").build();
        final Throwable cause = new SQLException("Database exception 'uk_cars_license_number' violated !");
        Throwable exception = factory.createException(violation, cause);
        // Ensure we created an instance of the correct type
        assertTrue(exception instanceof UsernameAlreadyExistsException);
        // Ensure the correct constructor was invoked (violation + cause)
        assertEquals("Unique key 'uk_cars_license_number' was violated.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(factory, ((UsernameAlreadyExistsException) exception).getExceptionFactory());
    }

    /**
     * Even third party exception classes can be used, as long as they have a supported constructor.
     */
    @Test
    public void testInstantiateThirdPartyException() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(IllegalStateException.class);
        DatabaseConstraintViolation violation = violaton(UNIQUE_KEY).constraint("uk_cars_license_number").build();
        final Throwable cause = new SQLException("Database exception 'uk_cars_license_number' violated !");
        Throwable exception = factory.createException(violation, cause);
        // The only supported constructor is (Throwable)
        assertTrue(exception instanceof IllegalStateException);
        assertEquals(cause, exception.getCause());
    }

    /**
     * No argument constructors are also supported, but only used if there are no other supported constructors.
     */
    @Test
    public void testInstantiateNullary() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(NoArgException.class);
        DatabaseConstraintViolation violation = violaton(UNIQUE_KEY).build();
        final Throwable cause = new SQLException("Database exception 'uk_cars_license_number' violated !");
        Throwable exception = factory.createException(violation, cause);
        assertTrue(exception instanceof NoArgException);
    }

    /**
     * Whenever an exception has no supported constructors at all, we throw a runtime exception.
     */
    @Test(expected = NoSuchElementException.class)
    public void testNoMatchingConstructor() {
        new ReflectionConstraintExceptionFactory(UnsupportedArgException.class);
    }

    public static class NoArgException extends RuntimeException {

        public NoArgException() {
            super();
        }
    }

    public static class UnsupportedArgException extends RuntimeException {

        public UnsupportedArgException(BigDecimal bigDecimal) {
            super();
        }
        
    }

}
