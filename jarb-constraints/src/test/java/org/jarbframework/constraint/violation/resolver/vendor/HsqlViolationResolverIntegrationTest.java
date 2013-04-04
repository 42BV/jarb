package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.FOREIGN_KEY;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("hsqldb")
@RunWith(SpringJUnit4ClassRunner.class)
public class HsqlViolationResolverIntegrationTest extends ViolationResolverIntegrationTestTemplate {
 
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
    
}
