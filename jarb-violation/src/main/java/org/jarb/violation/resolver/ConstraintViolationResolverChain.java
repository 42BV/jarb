package org.jarb.violation.resolver;

import java.util.ArrayList;
import java.util.List;

import org.jarb.violation.ConstraintViolation;

/**
 * Chain of responsiblity for constraint violation resolvers. Whenever a violation
 * resolver could not resolve the violation, our next resolver is invoked. This
 * process continues until a valid constraint violation gets resolved, or we run
 * out of violation resolvers.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class ConstraintViolationResolverChain implements ConstraintViolationResolver {
    private final List<ConstraintViolationResolver> violationResolvers;

    /**
     * Construct a new {@link ConstraintViolationResolverChain}.
     */
    public ConstraintViolationResolverChain() {
        violationResolvers = new ArrayList<ConstraintViolationResolver>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintViolation resolve(Throwable throwable) {
        for (ConstraintViolationResolver violationResolver : violationResolvers) {
            ConstraintViolation violation = violationResolver.resolve(throwable);
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
    public ConstraintViolationResolverChain addToChain(ConstraintViolationResolver violationResolver) {
        violationResolvers.add(violationResolver);
        return this;
    }
}
