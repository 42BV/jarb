package org.jarbframework.constraint.violation.resolver.vendor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolverFactory;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public abstract class ViolationResolverIntegrationTestTemplate {
    
    private DatabaseConstraintViolationResolver violationResolver;

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUpResolver() {
        violationResolver = new DatabaseConstraintViolationResolverFactory().build(dataSource);
    }
    
    protected DatabaseConstraintViolation persistWithViolation(final Object object) {
        try {
            persist(object);
            throw new AssertionError("Expected a runtime exception while persisting.");
        } catch (RuntimeException e) {
            DatabaseConstraintViolation violation = resolve(e);
            if (violation == null) {
                throw new IllegalStateException("Could not determine violation", e);
            }
            return violation;
        }
    }

    protected void persist(final Object object) {
        entityManager.persist(object);
    }

    protected DatabaseConstraintViolation resolve(RuntimeException exception) {
        return violationResolver.resolve(exception);
    }

}
