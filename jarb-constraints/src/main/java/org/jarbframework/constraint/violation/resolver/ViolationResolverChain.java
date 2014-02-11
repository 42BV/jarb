package org.jarbframework.constraint.violation.resolver;

import static org.jarbframework.utils.Asserts.notNull;

import java.util.Collection;
import java.util.LinkedList;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.DatabaseProduct;
import org.jarbframework.utils.DatabaseProductSpecific;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chain of responsibility for constraint violation resolvers. Whenever a violation
 * resolver could not resolve the violation, our next resolver is invoked. This
 * process continues until a valid constraint violation gets resolved, or we run
 * out of violation resolvers, in which case a {@code null} is returned.
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
        resolvers.add(notNull(resolver, "Cannot add a null resolver to the chain."));
        logger.debug("Registered resolver {} to chain.", resolver);
        return this;
    }
    
    /**
     * Add a violation resolver to the back of this chain, when supported.
     * @param resolver violation resolver instance we are adding
     * @param databaseProduct the database product
     * @return {@code this} instance, enabling the use of method chaining
     */
    public ViolationResolverChain addToChainWhenSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct databaseProduct) {
    	if (isSupported(resolver, databaseProduct)) {
            addToChain(resolver);
        }
    	return this;
    }
    
    private boolean isSupported(DatabaseConstraintViolationResolver resolver, DatabaseProduct databaseProduct) {
        boolean supported = true;
        if (resolver instanceof DatabaseProductSpecific) {
            supported = ((DatabaseProductSpecific) resolver).supports(databaseProduct);
        }
        return supported;
    }
    
}
