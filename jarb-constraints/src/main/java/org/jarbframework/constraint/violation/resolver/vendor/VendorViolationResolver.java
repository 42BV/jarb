package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.RootCauseMessageViolationResolver;
import org.jarbframework.constraint.violation.resolver.vendor.ViolationMessagePatterns.ViolationBuilder;

public abstract class VendorViolationResolver extends RootCauseMessageViolationResolver implements DatabaseProductSpecific {

    private final ViolationMessagePatterns patterns = new ViolationMessagePatterns();

    @Override
    protected final DatabaseConstraintViolation resolve(String rootCauseMessage) {
        return patterns.resolve(rootCauseMessage);
    }
    
    protected void registerPattern(String regex, ViolationBuilder builder) {
        patterns.register(regex, builder);
    }
    
}
