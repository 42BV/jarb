package org.jarbframework.constraint.violation.factory.custom;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.jarbframework.constraint.violation.domain.CarAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableConstraintExceptionFactoryTest {
    
    private final DatabaseConstraintViolation violation = new DatabaseConstraintViolation(UNIQUE_KEY, "uk_cars_license_number");

    private ConfigurableConstraintExceptionFactory exceptionFactory;

    @Before
    public void setUp() {
        exceptionFactory = new ConfigurableConstraintExceptionFactory();
    }

    @Test
    public void testCustomException() {
        exceptionFactory.register(violation.getConstraintName(), CarAlreadyExistsException.class);
        Throwable exception = exceptionFactory.createException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

    @Test
    public void testDefaultException() {
        Throwable exception = exceptionFactory.createException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }
    
    @Test
    public void testRegisterByAnnotation() {
        exceptionFactory.registerAll(CarAlreadyExistsException.class.getPackage().getName());
        Throwable exception = exceptionFactory.createException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

}
