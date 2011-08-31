package org.jarb.violation.factory;

import static org.jarb.violation.DatabaseConstraintViolation.violation;
import static org.jarb.violation.DatabaseConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertTrue;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.UniqueKeyViolationException;
import org.jarb.violation.domain.LicenseNumberAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableViolationExceptionFactoryTest {
    private ConfigurableViolationExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new ConfigurableViolationExceptionFactory();
    }

    /**
     * Assert that we provide default exception factory behaviour, whenever no
     * custom exception factory was registered for that violation.
     */
    @Test
    public void testDefaultException() {
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(UNIQUE_KEY);
        violationBuilder.setConstraintName("uk_some_table_some_column");
        Throwable exception = factory.createException(violationBuilder.build(), null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }

    /**
     * Assert that custom exceptions can be provided, whenever registered.
     */
    @Test
    public void testCustomException() {
        factory.registerException("uk_cars_license", LicenseNumberAlreadyExistsException.class);
        DatabaseConstraintViolation.DatabaseConstraintViolationBuilder violationBuilder = violation(UNIQUE_KEY);
        violationBuilder.setConstraintName("uk_cars_license");
        Throwable exception = factory.createException(violationBuilder.build(), null);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }

}
