package nl._42.jarb.constraint.violation.factory;

import nl._42.jarb.domain.CarAlreadyExistsException;
import nl._42.jarb.domain.CarAlreadyExistsExceptionFactory;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectionConstraintExceptionFactoryTest {
    
    private final DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.UNIQUE_KEY, "uk_cars_license_number");
    
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
    @Test
    public void testNoMatchingConstructor() {
        Assertions.assertThrows(IllegalStateException.class, () ->
            new ReflectionConstraintExceptionFactory(UnsupportedArgException.class)
        );
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
