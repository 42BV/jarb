package org.jarb.violation.factory;

import static org.jarb.violation.ConstraintViolation.createViolation;
import static org.jarb.violation.ConstraintViolationType.UNIQUE_KEY;
import static org.junit.Assert.assertTrue;

import org.jarb.violation.ConstraintViolation;
import org.jarb.violation.UniqueKeyViolationException;
import org.jarb.violation.domain.LicenseNumberAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;

public class ConfigurableConstraintViolationExceptionFactoryTest {
    private ConfigurableConstraintViolationExceptionFactory factory;

    @Before
    public void setUp() {
        factory = new ConfigurableConstraintViolationExceptionFactory();
    }

    /**
     * Assert that we provide default exception factory behaviour, whenever no
     * custom exception factory was registered for that violation.
     */
    @Test
    public void testDefaultException() {
        ConstraintViolation.Builder violationBuilder = createViolation(UNIQUE_KEY);
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
        ConstraintViolation.Builder violationBuilder = createViolation(UNIQUE_KEY);
        violationBuilder.setConstraintName("uk_cars_license");
        Throwable exception = factory.createException(violationBuilder.build(), null);
        assertTrue(exception instanceof LicenseNumberAlreadyExistsException);
    }

}
