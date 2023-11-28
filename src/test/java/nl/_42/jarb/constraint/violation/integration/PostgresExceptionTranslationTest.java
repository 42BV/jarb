package nl._42.jarb.constraint.violation.integration;

import nl._42.jarb.constraint.violation.CheckFailedException;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.domain.Car;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("postgresql")
public class PostgresExceptionTranslationTest extends AbstractExceptionTranslationTest {

    @Test
    public void testFunction() {
        Car car = new Car("123456");
        car.setPrice(0.0);

        CheckFailedException exception = assertThrows(CheckFailedException.class, () ->
            carRepository.save(car)
        );

        DatabaseConstraintViolation violation = exception.getViolation();
        assertEquals(DatabaseConstraintType.CHECK_FAILED, violation.getConstraintType());
        assertEquals("fn_validate_cars", violation.getConstraintName());
        assertEquals("Cars are never free", violation.getValue());
    }

}
