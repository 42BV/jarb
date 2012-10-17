package org.jarbframework.constraint.violation.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.exception.ConstraintViolationException;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.junit.Before;
import org.junit.Test;

public class HibernateViolationResolverTest {

    private HibernateViolationResolver resolver;
    
    @Before
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
    public void testIgnoreOtherThrowables() {
        assertNull(resolver.resolve(new RuntimeException()));
    }
 
    // TODO: Figure out how to test with the exception not on our classpath
    
}
