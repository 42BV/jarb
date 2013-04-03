package org.jarbframework.constraint.violation;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.domain.CarAlreadyExistsException;
import org.jarbframework.constraint.violation.domain.CarInactiveException;
import org.jarbframework.constraint.violation.domain.CarRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:translation-context.xml" })
public class ConstraintViolationExceptionTranslatingBeanPostProcessorTest {

    @Autowired
    private CarRepository users;

    /**
     * HSQL throws a native exception, stating that "uk_cars_license_number" was violated.
     * Because we registered a custom exception factory for that constraint, our custom
     * "car already exists" exception should be thrown.
     */
    @Test
    public void testUniqueWithCustomException() {
        Car car = new Car("iarpro");
        users.add(car);
        Car sameCar = new Car("iarpro");
        try {
            users.add(sameCar);
            fail("Expected a license number already exists exception");
        } catch (CarAlreadyExistsException e) {
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
            assertEquals("uk_cars_license_number", violation.getConstraintName());
        }
    }

    /**
     * HSQL throws a native exception, starting that license number cannot be null. Our
     * translator should convert this into the default "not null" violation exception.
     */
    @Test
    public void testNotNullDefaultException() {
        Car carWithoutLicense = new Car(null);
        try {
            users.add(carWithoutLicense);
            fail("Expected a not null exception");
        } catch (NotNullViolationException e) {
            assertEquals("Column 'name' cannot be null.", e.getMessage());
            DatabaseConstraintViolation violation = e.getViolation();
            assertEquals(NOT_NULL, violation.getConstraintType());
            assertEquals("name", violation.getColumnName());
        }
    }

    /**
     * Checked exceptions should not be affected by our translation.
     * @throws CarInactiveException always, but expected
     */
    @Test(expected = CarInactiveException.class)
    public void testCheckedExceptionUnaffected() throws CarInactiveException {
        users.throwCheckedException();
    }

    /**
     * Unrelated runtime exceptions should always remain unaffected by our translation.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testOtherRuntimeExceptionUnaffected() {
        users.throwUnsupportedOperationException();
    }

}
