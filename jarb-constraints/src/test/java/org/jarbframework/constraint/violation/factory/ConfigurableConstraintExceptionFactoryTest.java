package org.jarbframework.constraint.violation.factory;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.jarbframework.constraint.violation.factory.NameMatchingStrategy.EXACT;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.domain.CarAlreadyExistsException;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.UniqueKeyViolationException;
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
        exceptionFactory.register(violation.getConstraintName(), EXACT, CarAlreadyExistsException.class);
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

    @Test
    public void testDefaultException() {
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }
    
    @Test
    public void testRegisterByAnnotation() {
        exceptionFactory.registerAll(CarAlreadyExistsException.class.getPackage().getName());
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

}
