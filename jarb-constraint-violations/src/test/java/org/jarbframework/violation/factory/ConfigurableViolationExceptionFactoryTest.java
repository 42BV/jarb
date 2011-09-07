package org.jarbframework.violation.factory;

import static org.jarbframework.violation.DatabaseConstraintViolation.violation;
import static org.jarbframework.violation.DatabaseConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertTrue;

import org.jarbframework.violation.UniqueKeyViolationException;
import org.jarbframework.violation.domain.LicenseNumberAlreadyExistsException;
import org.jarbframework.violation.factory.ConfigurableViolationExceptionFactory;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableViolationExceptionFactoryTest {
    private ConfigurableViolationExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new ConfigurableViolationExceptionFactory();
    }
    
    /**
     * Assert that custom exceptions can be provided, whenever registered.
     */
    @Test
    public void testCustomException() {
        factory.registerException("uk_cars_license", LicenseNumberAlreadyExistsException.class);
        Throwable exception = factory.createException(violation(UNIQUE_KEY).named("uk_cars_license").build(), null);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }   
    
    /**
     * Even when using a regex expression.
     */
    @Test
    public void testCustomExceptionRegex() {
        factory.registerException("uk_(.)+_license", LicenseNumberAlreadyExistsException.class);
        Throwable exception = factory.createException(violation(UNIQUE_KEY).named("uk_cars_license").build(), null);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }    

    /**
     * Assert that we provide default exception factory behaviour, whenever no
     * custom exception factory was registered for that violation.
     */
    @Test
    public void testDefaultException() {
        Throwable exception = factory.createException(violation(UNIQUE_KEY).named("uk_some_table_some_column").build(), null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }



}
