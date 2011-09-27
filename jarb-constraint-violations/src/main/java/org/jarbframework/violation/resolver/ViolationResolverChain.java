package org.jarbframework.violation.resolver;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.violation.DatabaseConstraintViolation;

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
    private final List<DatabaseConstraintViolationResolver> violationResolvers;

    /**
     * Construct a new {@link ViolationResolverChain}.
     */
    public ViolationResolverChain() {
        violationResolvers = new ArrayList<DatabaseConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        for (DatabaseConstraintViolationResolver violationResolver : violationResolvers) {
            DatabaseConstraintViolation violation = violationResolver.resolve(throwable);
            if (violation != null) {
                return violation;
            }
        }
        return null; // Could not determine a constraint violation
    }

    /**
     * Add a violation resolver to the back of this chain.
     * @param violationResolver violation resolver instance we are adding
     * @return  this instance, enabling the use of method chaining
     */
    public ViolationResolverChain addToChain(DatabaseConstraintViolationResolver violationResolver) {
        violationResolvers.add(violationResolver);
        return this;
    }
}
