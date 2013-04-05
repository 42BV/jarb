package org.jarbframework.constraint.violation.resolver;

import java.util.Collection;
import java.util.LinkedList;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chain of responsiblity for constraint violation resolvers. Whenever a violation
 * resolver could not resolve the violation, our next resolver is invoked. This
 * process continues until a valid constraint violation gets resolved, or we run
 * out of violation resolvers.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class ViolationResolverChain implements DatabaseConstraintViolationResolver {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Collection<DatabaseConstraintViolationResolver> resolvers;

    public ViolationResolverChain() {
        resolvers = new LinkedList<DatabaseConstraintViolationResolver>();
    }

    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        for (DatabaseConstraintViolationResolver resolver : resolvers) {
            logger.debug("Attempting to resolve violation with: {}", resolver);
            DatabaseConstraintViolation violation = resolver.resolve(throwable);
            if (violation != null) {
                logger.debug("Violation was resolved by: {}", resolver);
                return violation;
            }
        }
        
        return null;
    }

    /**
     * Add a violation resolver to the back of this chain.
     * @param resolver violation resolver instance we are adding
     * @return {@code this} instance, enabling the use of method chaining
     */
    public ViolationResolverChain addToChain(DatabaseConstraintViolationResolver resolver) {
        Asserts.notNull(resolver, "Cannot add a null resolver to the chain.");
        resolvers.add(resolver);
        logger.trace("Added {} to resolver chain.", resolver);
        return this;
    }
    
}
