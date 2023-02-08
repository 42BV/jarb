package nl._42.jarb.constraint.violation.integration;

import nl._42.jarb.constraint.ConstraintsTestConfig;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test that exceptions are translated into constraint violation exceptions.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Transactional
@SpringBootTest(classes = ConstraintsTestConfig.class)
public abstract class AbstractExceptionTranslationTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    public void testUniqueKey() {
        carRepository.save(new Car("123456"));
        
        try {
            carRepository.save(new Car("123456"));
            Assertions.fail("Expected a CarAlreadyExistsException.");
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
            Assertions.fail("Expected a NotNullViolationException.");
        } catch (NotNullViolationException nnve) {
            DatabaseConstraintViolation violation = nnve.getViolation();
            assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
        }
    }
    
    @Test
    public void testForeignKey() {
    	Car car = new Car("123456");
    	car.setOwnerId(Long.valueOf(-1));
    	try {
            carRepository.save(car);
            Assertions.fail("Expected a InvalidTypeException.");
        } catch (ForeignKeyViolationException fkve) {
            DatabaseConstraintViolation violation = fkve.getViolation();
            assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
        }
    }
    
    @Test
    public void testLengthExceeded() {
    	try {
            carRepository.save(new Car("1234567"));
            Assertions.fail("Expected a LengthExceededException.");
        } catch (LengthExceededException lee) {
            DatabaseConstraintViolation violation = lee.getViolation();
            assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
        }
    }
    
    @Test
    public void testInvalidType() {
    	Car car = new Car("123456");
    	car.setActive("Not a boolean");
    	try {
            carRepository.save(car);
            Assertions.fail("Expected a InvalidTypeException.");
        } catch (InvalidTypeException ite) {
            DatabaseConstraintViolation violation = ite.getViolation();
            assertEquals(DatabaseConstraintType.INVALID_TYPE, violation.getConstraintType());
        }
    }

    @Test
    public void testCheckedException() {
        Assertions.assertThrows(CarInactiveException.class, () ->
            carRepository.throwCheckedException()
        );
    }

    @Test
    public void testUnknownRuntimeException() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
            carRepository.throwUnsupportedOperationException()
        );
    }

}
