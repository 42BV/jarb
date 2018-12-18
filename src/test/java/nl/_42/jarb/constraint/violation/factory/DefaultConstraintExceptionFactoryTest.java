package nl._42.jarb.constraint.violation.factory;

import static nl._42.jarb.constraint.violation.DatabaseConstraintViolation.builder;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.CHECK_FAILED;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static nl._42.jarb.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import nl._42.jarb.constraint.violation.CheckFailedException;
import nl._42.jarb.constraint.violation.DatabaseConstraintType;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolationException;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.InvalidTypeException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;

import nl._42.jarb.constraint.violation.CheckFailedException;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.constraint.violation.DatabaseConstraintViolationException;
import nl._42.jarb.constraint.violation.ForeignKeyViolationException;
import nl._42.jarb.constraint.violation.InvalidTypeException;
import nl._42.jarb.constraint.violation.LengthExceededException;
import nl._42.jarb.constraint.violation.NotNullViolationException;
import nl._42.jarb.constraint.violation.UniqueKeyViolationException;
import org.junit.Before;
import org.junit.Test;

public class DefaultConstraintExceptionFactoryTest {
    
    private DefaultConstraintExceptionFactory exceptionFactory;

    @Before
    public void setUp() {
        exceptionFactory = new DefaultConstraintExceptionFactory();
    }

    @Test
    public void testCheckFailed() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.CHECK_FAILED).constraint("ck_name_cannot_be_henk").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof CheckFailedException);
        assertEquals("Check 'ck_name_cannot_be_henk' failed.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testUniqueKeyViolated() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.UNIQUE_KEY).constraint("uk_persons_name").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof UniqueKeyViolationException);
        assertEquals("Unique key 'uk_persons_name' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testForeignKeyViolated() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.FOREIGN_KEY).constraint("fk_persons_parent").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof ForeignKeyViolationException);
        assertEquals("Foreign key 'fk_persons_parent' was violated.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testNotNullViolated() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.NOT_NULL).column("name").build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof NotNullViolationException);
        assertEquals("Column 'name' cannot be null.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testInvalidType() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.INVALID_TYPE).build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof InvalidTypeException);
        assertEquals("Column is of an invalid type.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

    @Test
    public void testLengthExceeded() {
        DatabaseConstraintViolation violation = DatabaseConstraintViolation.builder(DatabaseConstraintType.LENGTH_EXCEEDED).build();
        DatabaseConstraintViolationException exception = exceptionFactory.buildException(violation, null);
        assertTrue(exception instanceof LengthExceededException);
        assertEquals("Column maximum length was exceeded.", exception.getMessage());
        assertEquals(violation, exception.getViolation());
    }

}
