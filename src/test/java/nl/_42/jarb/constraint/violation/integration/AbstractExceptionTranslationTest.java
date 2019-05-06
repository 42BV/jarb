package nl._42.jarb.constraint.violation.integration;

import static nl._42.jarb.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import nl._42.jarb.constraint.ConstraintsTestConfig;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.InvalidTypeException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;

import nl._42.jarb.constraint.domain.Car;
import nl._42.jarb.constraint.domain.CarAlreadyExistsException;
import nl._42.jarb.constraint.domain.CarInactiveException;
import nl._42.jarb.constraint.domain.CarRepository;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.InvalidTypeException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;
import org.junit.Assert;
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
@ContextConfiguration(classes = ConstraintsTestConfig.class)
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
            Assert.assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
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
            Assert.assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
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
            Assert.assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        }
    }
    
    @Test
    public void testLengthExceeded() {
    	try {
            carRepository.save(new Car("1234567"));
            fail("Expected a LengthExceededException.");
        } catch (LengthExceededException lee) {
            DatabaseConstraintViolation violation = lee.getViolation();
            Assert.assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
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
            Assert.assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
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
