package org.jarbframework.constraint.violation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.jarbframework.constraint.domain.Car;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.MessageBasedViolationResolver;
import org.jarbframework.constraint.violation.resolver.RootCauseViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.HsqlViolationResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Translate exceptions thrown by an actual HSQL database.
 * 
 * @author Jeroen van Schagen
 * @since 18-05-2011
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("hsqldb")
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class DatabaseConstraintExceptionTranslatorTest {
    
    private DatabaseConstraintExceptionTranslator exceptionTranslator;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUpResolver() {
        MessageBasedViolationResolver messageResolver = new HsqlViolationResolver();
        DatabaseConstraintViolationResolver violationResolver = new RootCauseViolationResolver(messageResolver);
        exceptionTranslator = new DatabaseConstraintExceptionTranslator(violationResolver);
    }

    /**
     * Enforce a 'null-null' violation on our HSQL database. The exception should be
     * translated into a {@link NotNullViolationException}, containing all error info.
     */
    @Test
    public void testClean() {
        Car car = new Car(null);
        try {
            entityManager.persist(car);
            fail("Expected a runtime exception");
        } catch (final PersistenceException exception) {
            Throwable violationException = exceptionTranslator.translate(exception);
            assertTrue(violationException instanceof NotNullViolationException);
            assertEquals("Column 'license_number' cannot be null.", violationException.getMessage());
        }
    }

    /**
     * Exceptions that have nothing to do with constraint violations should result in {@link null}.
     */
    @Test
    public void testDoNothing() {
        assertNull(exceptionTranslator.translate(new IllegalArgumentException("Something went wrong!")));
    }

}
