package org.jarbframework.constraint.violation.factory;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.jarbframework.constraint.domain.CarAlreadyExistsException;
import org.jarbframework.constraint.domain.CarAlreadyExistsExceptionFactory;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.reflection.ReflectionConstraintExceptionFactory;
import org.junit.Test;

public class ReflectionConstraintExceptionFactoryTest {
    
    private final DatabaseConstraintViolation violation = new DatabaseConstraintViolation(UNIQUE_KEY, "uk_cars_license_number");
    
    private final Throwable cause = new SQLException("Database exception 'uk_cars_license_number' violated !");

    /**
     * Custom exception classes should be initialized using the best available constructor.
     * The best constructor has the most supported parameter types.
     */
    @Test
    public void testInstantiateException() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(CarAlreadyExistsException.class);
        
        Throwable exception = factory.buildException(violation, cause);
        assertTrue(exception instanceof CarAlreadyExistsException);
        assertEquals("Unique key 'uk_cars_license_number' was violated.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(factory, ((CarAlreadyExistsException) exception).getExceptionFactory());
    }

    /**
     * Even third party exception classes can be used, as long as they have a supported constructor.
     */
    @Test
    public void testInstantiateThirdPartyException() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(IllegalStateException.class);

        Throwable exception = factory.buildException(violation, cause);
        assertTrue(exception instanceof IllegalStateException);
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    public void testInstantiateFactory() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(CarAlreadyExistsExceptionFactory.class);
        
        Throwable exception = factory.buildException(violation, cause);
        assertTrue(exception instanceof CarAlreadyExistsException);
        assertEquals("Unique key 'uk_cars_license_number' was violated.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(((CarAlreadyExistsException) exception).getExceptionFactory() instanceof CarAlreadyExistsExceptionFactory);
    }

    /**
     * No argument constructors are also supported, but only used if there are no other supported constructors.
     */
    @Test
    public void testInstantiateNoArg() {
        DatabaseConstraintExceptionFactory factory = new ReflectionConstraintExceptionFactory(NoArgException.class);

        Throwable exception = factory.buildException(violation, cause);
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
