package org.jarbframework.constraint.violation.resolver.vendor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.jarbframework.constraint.violation.resolver.MessageBasedViolationResolver;
import org.jarbframework.constraint.violation.resolver.RootCauseViolationResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public abstract class ViolationResolverIntegrationTestTemplate {
    
    private final DatabaseConstraintViolationResolver violationResolver;

    @PersistenceContext
    private EntityManager entityManager;

    public ViolationResolverIntegrationTestTemplate(MessageBasedViolationResolver messageResolver) {
        this.violationResolver = new RootCauseViolationResolver(messageResolver);
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
        return violationResolver.resolve(exception);
    }

}
