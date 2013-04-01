package org.jarbframework.constraint.violation.resolver;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

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
    
    private final List<DatabaseConstraintViolationResolver> resolvers;

    public ViolationResolverChain() {
        resolvers = new ArrayList<DatabaseConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation result = null;
        for (DatabaseConstraintViolationResolver resolver : resolvers) {
            DatabaseConstraintViolation violation = resolver.resolve(throwable);
            if (violation != null) {
                result = violation;
                break;
            }
        }
        return result;
    }

    /**
     * Add a violation resolver to the back of this chain.
     * @param resolver violation resolver instance we are adding
     * @return {@code this} instance, enabling the use of method chaining
     */
    public ViolationResolverChain addToChain(DatabaseConstraintViolationResolver resolver) {
        if(resolver != null) {
            resolvers.add(resolver);
        }
        return this;
    }
    
}
