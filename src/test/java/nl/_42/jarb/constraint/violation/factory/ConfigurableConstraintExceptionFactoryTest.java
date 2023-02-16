package nl._42.jarb.constraint.violation.factory;

import nl._42.jarb.constraint.domain.CarAlreadyExistsException;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurableConstraintExceptionFactoryTest {
    
    private final DatabaseConstraintViolation violation = new DatabaseConstraintViolation(DatabaseConstraintType.UNIQUE_KEY, "uk_cars_license_number");

    private ConfigurableConstraintExceptionFactory exceptionFactory;

    @BeforeEach
    public void setUp() {
        exceptionFactory = new ConfigurableConstraintExceptionFactory();
    }

    @Test
    public void testCustomException() {
        exceptionFactory.register(new String[]{violation.getConstraintName()}, NameMatchingStrategy.EXACT, CarAlreadyExistsException.class);
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

    @Test
    public void testDefaultException() {
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
    }
    
    @Test
    public void testRegisterByAnnotation() {
        exceptionFactory.registerAll(CarAlreadyExistsException.class.getPackage().getName());
        Throwable exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CarAlreadyExistsException);
    }

}
