package nl._42.jarb.constraint.violation.resolver;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HibernateViolationResolverTest {

    private HibernateViolationResolver resolver;
    
    @BeforeEach
    public void setUp() {
        resolver = new HibernateViolationResolver();
    }
    
    @Test
    public void testResolveConstraintName() {
        ConstraintViolationException exception = new ConstraintViolationException("some message", null, "my_constraint");
        DatabaseConstraintViolation violation = resolver.resolve(exception);
        assertEquals("my_constraint", violation.getConstraintName());
    }
    
    @Test
    public void testIgnoreOtherThrowable() {
        assertNull(resolver.resolve(new RuntimeException()));
    }

}
