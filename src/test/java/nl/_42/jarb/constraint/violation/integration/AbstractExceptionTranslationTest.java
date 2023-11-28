package nl._42.jarb.constraint.violation.integration;

import nl._42.jarb.Application;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;
import nl._42.jarb.domain.Car;
import nl._42.jarb.domain.CarAlreadyExistsException;
import nl._42.jarb.domain.CarInactiveException;
import nl._42.jarb.domain.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test that exceptions are translated into constraint violation exceptions.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
@Transactional
@SpringBootTest(classes = Application.class)
public abstract class AbstractExceptionTranslationTest {

    @Autowired
    protected CarRepository carRepository;

    @Test
    public void testUniqueKey() {
        carRepository.save(new Car("123456"));

        CarAlreadyExistsException exception = assertThrows(CarAlreadyExistsException.class, () ->
            carRepository.save(new Car("123456"))
        );

        DatabaseConstraintViolation violation = exception.getViolation();
        assertEquals(DatabaseConstraintType.UNIQUE_KEY, violation.getConstraintType());
        assertEquals("uk_cars_license_number", violation.getConstraintName());
    }

    @Test
    public void testNotNull() {
        NotNullViolationException exception = assertThrows(NotNullViolationException.class, () ->
            carRepository.save(new Car(null))
        );

        DatabaseConstraintViolation violation = exception.getViolation();
        assertEquals(DatabaseConstraintType.NOT_NULL, violation.getConstraintType());
    }
    
    @Test
    public void testForeignKey() {
    	Car car = new Car("123456");
    	car.setOwnerId(Long.valueOf(-1));

        ForeignKeyViolationException exception = assertThrows(ForeignKeyViolationException.class, () ->
            carRepository.save(car)
        );

        DatabaseConstraintViolation violation = exception.getViolation();
        assertEquals(DatabaseConstraintType.FOREIGN_KEY, violation.getConstraintType());
    }
    
    @Test
    public void testLengthExceeded() {
        LengthExceededException exception = assertThrows(LengthExceededException.class, () ->
            carRepository.save(new Car("1234567"))
        );

        DatabaseConstraintViolation violation = exception.getViolation();
        assertEquals(DatabaseConstraintType.LENGTH_EXCEEDED, violation.getConstraintType());
    }

    @Test
    public void testCheckedException() {
        assertThrows(CarInactiveException.class, () ->
            carRepository.throwCheckedException()
        );
    }

    @Test
    public void testUnknownRuntimeException() {
        assertThrows(UnsupportedOperationException.class, () ->
            carRepository.throwUnsupportedOperationException()
        );
    }

}
