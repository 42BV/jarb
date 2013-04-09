package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.RootCauseMessageViolationResolver;
import org.jarbframework.constraint.violation.resolver.product.DatabaseProductSpecific;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.ViolationBuilder;

/**
 * Template for vendor specific violation resolvers. Extend from this
 * class and register the desired message patterns.
 * @author Jeroen van Schagen
 */
public abstract class VendorViolationResolver extends RootCauseMessageViolationResolver implements DatabaseProductSpecific {

    private final ViolationMessagePatterns patterns = new ViolationMessagePatterns();

    @Override
    protected final DatabaseConstraintViolation resolve(String rootCauseMessage) {
        return patterns.resolve(rootCauseMessage);
    }
    
    /**
     * Register a new vendor specific message pattern.
     * @param regex the regular expression that matches our message
     * @param builder the builder of our violation
     */
    protected void registerPattern(String regex, ViolationBuilder builder) {
        patterns.register(regex, builder);
    }
    
}
