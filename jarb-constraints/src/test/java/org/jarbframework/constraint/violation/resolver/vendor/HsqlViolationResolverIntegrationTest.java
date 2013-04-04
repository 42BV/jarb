package org.jarbframework.constraint.violation.resolver.vendor;

import static org.jarbframework.constraint.violation.DatabaseConstraintType.INVALID_TYPE;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.LENGTH_EXCEEDED;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.NOT_NULL;
import static org.jarbframework.constraint.violation.DatabaseConstraintType.UNIQUE_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class HsqlViolationResolverIntegrationTest {
    
    private DatabaseConstraintViolationResolver violationResolver;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUpResolver() {
        violationResolver = new DatabaseConstraintViolationResolverFactory().build(dataSource);
    }

    /**
     * Column "license number" cannot be null.
     */
    @Test
    public void testNotNull() {
        Car car = new Car(null);
        try {
            entityManager.persist(car);
            fail("Expected a runtime exception");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = violationResolver.resolve(e);
            assertEquals(NOT_NULL, violation.getConstraintType());
            assertTrue(violation.getConstraintName().startsWith("sys_ct_"));
            assertEquals("cars", violation.getTableName());
            assertEquals("license_number", violation.getColumnName());
        }
    }

    /**
     * Column "name" is expected to be unique.
     */
    @Test
    public void testUniqueKey() {
        entityManager.persist(new Car("admin"));
        try {
            entityManager.persist(new Car("admin"));
            fail("Expected a runtime exception");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = violationResolver.resolve(e);
            assertEquals(UNIQUE_KEY, violation.getConstraintType());
            assertEquals("uk_cars_license_number", violation.getConstraintName());
            assertEquals("cars", violation.getTableName());
        }
    }

    /**
     * Column "license number" has a maximum length of '6'.
     */
    @Test
    public void testLengthExceeded() {
        try {
            entityManager.persist(new Car("1234567"));
            fail("Expected a runtime exception");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = violationResolver.resolve(e);
            assertEquals(LENGTH_EXCEEDED, violation.getConstraintType());
        }
    }

    /**
     * Column "active" is a boolean in the database, not a string.
     */
    @Test
    public void testInvalidType() {
        Car jeroen = new Car("Jeroen");
        jeroen.setActive("not a boolean");
        try {
            entityManager.persist(jeroen);
            fail("Expected a runtime exception");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = violationResolver.resolve(e);
            assertEquals(INVALID_TYPE, violation.getConstraintType());
        }
    }
    
    @Test
    public void testUnknown() {
        DatabaseConstraintViolation violation = violationResolver.resolve(new RuntimeException());
        assertNull(violation);
    }

}
