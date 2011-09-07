package org.jarbframework.violation.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.DatabaseConstraintViolationType;
import org.jarbframework.violation.NotNullViolationException;
import org.jarbframework.violation.domain.Car;
import org.jarbframework.violation.domain.CarRepository;
import org.jarbframework.violation.domain.GarageClosedException;
import org.jarbframework.violation.domain.LicenseNumberAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test that exceptions are translated into constraint violation exceptions.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:hsql-context.xml", "classpath:translation-context.xml" })
public class ConstraintViolationExceptionTranslatingBeanPostProcessorTest {

    @Autowired
    private CarRepository cars;

    /**
     * HSQL throws a native exception, stating that "uk_cars_license_number" was violated.
     * Because we registered a custom exception factory for that constraint, our custom
     * "license number already exists" exception should be thrown.
     */
    @Test
    public void testUniqueWithCustomException() {
        Car car = new Car();
        car.setLicenseNumber("iarpro");
        cars.add(car);
        Car carWithSameLicenseNumber = new Car();
        carWithSameLicenseNumber.setLicenseNumber("iarpro");
        try {
            cars.add(carWithSameLicenseNumber);
            fail("Expected a license number already exists exception");
        } catch (LicenseNumberAlreadyExistsException e) {
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(DatabaseConstraintViolationType.UNIQUE_KEY, violation.getViolationType());
            assertEquals("uk_cars_license_number", violation.getConstraintName());
        }
    }

    /**
     * HSQL throws a native exception, starting that license_number cannot be null. Our
     * translator should convert this into the default "not null" violation exception.
     */
    @Test
    public void testNotNullDefaultException() {
        Car carWithoutLicenseNumber = new Car();
        try {
            cars.add(carWithoutLicenseNumber);
            fail("Expected a not null exception");
        } catch (NotNullViolationException e) {
            assertEquals("Column 'license_number' cannot be null.", e.getMessage());
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(DatabaseConstraintViolationType.NOT_NULL, violation.getViolationType());
            assertEquals("license_number", violation.getColumnName());
        }
    }

    /**
     * Checked exceptions should not be affected by our translation.
     * @throws GarageClosedException always, but expected
     */
    @Test(expected = GarageClosedException.class)
    public void testCheckedExceptionUnaffected() throws GarageClosedException {
        cars.throwCheckedException();
    }

    /**
     * Unrelated runtime exceptions should always remain unaffected by our translation.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testOtherRuntimeExceptionUnaffected() {
        cars.throwUnsupportedOperationException();
    }

}
