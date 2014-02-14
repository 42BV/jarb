package org.jarbframework.constraint.violation.integration;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.DatabaseConstraintType;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.ForeignKeyViolationException;
import org.jarbframework.constraint.violation.InvalidTypeException;
import org.jarbframework.constraint.violation.LengthExceededException;
import org.jarbframework.constraint.violation.NotNullViolationException;
import org.jarbframework.constraint.violation.domain.CarAlreadyExistsException;
import org.jarbframework.constraint.violation.domain.CarInactiveException;
import org.jarbframework.constraint.violation.domain.CarRepository;
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
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public abstract class AbstractExceptionTranslationTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    public void testUniqueKey() {
        carRepository.save(new Car("123456"));
        
        try {
            carRepository.save(new Car("123456"));
            fail("Expected a CarAlreadyExistsException.");
        } catch (CarAlreadyExistsException caee) {
            DatabaseConstraintViolation violation = caee.getViolation();
            assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
            assertEquals("uk_cars_license_number", violation.getConstraintName());
        }
    }

    @Test
    public void testNotNull() {
        try {
            carRepository.save(new Car(null));
            fail("Expected a NotNullViolationException.");
        } catch (NotNullViolationException nnve) {            
            DatabaseConstraintViolation violation = nnve.getViolation();
            assertEquals(NOT_NULL, violation.getConstraintType());
        }
    }
    
    @Test
    public void testForeignKey() {
    	Car car = new Car("123456");
    	car.setOwnerId(Long.valueOf(-1));
    	try {
            carRepository.save(car);
            fail("Expected a InvalidTypeException.");
        } catch (ForeignKeyViolationException fkve) {            
            DatabaseConstraintViolation violation = fkve.getViolation();
            assertEquals(FOREIGN_KEY, violation.getConstraintType());
        }
    }
    
    @Test
    public void testLengthExceeded() {
    	try {
            carRepository.save(new Car("1234567"));
            fail("Expected a LengthExceededException.");
        } catch (LengthExceededException lee) {            
            DatabaseConstraintViolation violation = lee.getViolation();
            assertEquals(LENGTH_EXCEEDED, violation.getConstraintType());
        }
    }
    
    @Test
    public void testInvalidType() {
    	Car car = new Car("123456");
    	car.setActive("Not a boolean");
    	try {
            carRepository.save(car);
            fail("Expected a InvalidTypeException.");
        } catch (InvalidTypeException ite) {            
            DatabaseConstraintViolation violation = ite.getViolation();
            assertEquals(INVALID_TYPE, violation.getConstraintType());
        }
    }

    @Test(expected = CarInactiveException.class)
    public void testCheckedException() throws CarInactiveException {
        carRepository.throwCheckedException();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnknownRuntimeException() {
        carRepository.throwUnsupportedOperationException();
    }

}
