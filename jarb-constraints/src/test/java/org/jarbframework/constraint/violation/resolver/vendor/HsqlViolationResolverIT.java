package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("hsqldb")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class HsqlViolationResolverIT {
 
    private final DatabaseConstraintViolationResolver resolver = new HsqlViolationResolver();

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testNotNull() {
        DatabaseConstraintViolation violation = persistWithViolation(new Car(null));
        assertEquals(NOT_NULL, violation.getConstraintType());
        assertTrue(violation.getConstraintName().startsWith("sys_ct_"));
        assertEquals("cars", violation.getTableName());
        assertEquals("license_number", violation.getColumnName());
    }

    @Test
    public void testUniqueKey() {
        persist(new Car("car"));
        
        DatabaseConstraintViolation violation = persistWithViolation(new Car("car"));
        assertEquals(UNIQUE_KEY, violation.getConstraintType());
        assertEquals("uk_cars_license_number", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testForeignKey() {
        Car car = new Car("car");
        car.setOwnerId(Long.valueOf(-1));
        
        DatabaseConstraintViolation violation = persistWithViolation(car);
        assertEquals(FOREIGN_KEY, violation.getConstraintType());
        assertEquals("fk_cars_owner", violation.getConstraintName());
        assertEquals("cars", violation.getTableName());
    }

    @Test
    public void testLengthExceeded() {
        DatabaseConstraintViolation violation = persistWithViolation(new Car("1234567"));
        assertEquals(LENGTH_EXCEEDED, violation.getConstraintType());
    }

    @Test
    public void testInvalidType() {
        Car car = new Car("car");
        car.setActive("not a boolean");
        
        DatabaseConstraintViolation violation = persistWithViolation(car);
        assertEquals(INVALID_TYPE, violation.getConstraintType());
    }
    
    @Test
    public void testUnknown() {
        assertNull(resolve(new RuntimeException()));
    }

    protected DatabaseConstraintViolation persistWithViolation(final Object object) {
        try {
            persist(object);
            throw new AssertionError("Expected a runtime exception while persisting.");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = resolve(e);
            if (violation == null) {
                throw new IllegalStateException("Could not resolve violation from exception.", e);
            }
            return violation;
        }
    }

    protected void persist(final Object object) {
        entityManager.persist(object);
    }

    protected DatabaseConstraintViolation resolve(RuntimeException exception) {
        return resolver.resolve(exception);
    }
    
}
