package org.jarbframework.constraint.violation.factory.custom;

import static org.jarbframework.constraint.violation.DatabaseConstraintViolation.violaton;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.violation.UniqueKeyViolationException;
import org.jarbframework.constraint.violation.domain.UsernameAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableConstraintExceptionFactoryTest {
    
    private ConfigurableConstraintExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new ConfigurableConstraintExceptionFactory();
    }

    /**
     * Assert that custom exceptions can be provided, whenever registered.
     */
    @Test
    public void testCustomException() {
        factory.registerException("uk_cars_license", UsernameAlreadyExistsException.class);
        Throwable exception = factory.createException(violaton(UNIQUE_KEY).constraint("uk_cars_license").build(), null);
        assertTrue(exception instanceof UsernameAlreadyExistsException);
    }

    /**
     * Assert that we provide default exception factory behaviour, whenever no
     * custom exception factory was registered for that violation.
     */
    @Test
    public void testDefaultException() {
        Throwable exception = factory.createException(violaton(UNIQUE_KEY).constraint("uk_some_table_some_column").build(), null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }

}
