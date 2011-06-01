package org.jarb.violation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.jarb.violation.domain.Car;
import org.jarb.violation.factory.SimpleConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.ConstraintViolationResolver;
import org.jarb.violation.resolver.ConstraintViolationResolverFactory;
import org.jarb.violation.resolver.database.DatabaseResolver;
import org.jarb.violation.resolver.database.HibernateJpaDatabaseResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@ContextConfiguration(locations = { "classpath:hsql-context.xml" })
public class ConstraintViolationExceptionTranslatorTest {
    private ConstraintViolationExceptionTranslator translator;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUpResolver() {
        translator = new ConstraintViolationExceptionTranslator();
        DatabaseResolver databaseResolver = HibernateJpaDatabaseResolver.forEntityManager(entityManager);
        ConstraintViolationResolver violationResolver = ConstraintViolationResolverFactory.build(databaseResolver);
        translator.setViolationResolver(violationResolver);
        translator.setExceptionFactory(new SimpleConstraintViolationExceptionFactory());
    }

    /**
     * Enforce a 'null-null' violation on our HSQL database. The exception should be
     * translated into a {@link NotNullViolationException}, containing all error info.
     */
    @Test
    public void testClean() {
        Car car = new Car();
        try {
            entityManager.persist(car);
            fail("Expected a runtime exception");
        } catch (final PersistenceException exception) {
            Throwable violationException = translator.translateExceptionIfPossible(exception);
            assertTrue(violationException instanceof NotNullViolationException);
            assertEquals("Column 'LICENSE_NUMBER' cannot be null.", violationException.getMessage());
        }
    }

    /**
     * Exceptions that have nothing to do with constraint violations should result in {@link null}.
     */
    @Test
    public void testDoNothing() {
        final IllegalArgumentException exception = new IllegalArgumentException("Something went wrong!");
        assertNull(translator.translateExceptionIfPossible(exception));
    }

}
