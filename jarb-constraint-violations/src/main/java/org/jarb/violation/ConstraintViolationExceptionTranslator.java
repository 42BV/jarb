package org.jarb.violation;

import org.jarb.violation.factory.ConstraintViolationExceptionFactory;
import org.jarb.violation.factory.SimpleConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.ConstraintViolationResolver;
import org.springframework.util.Assert;

/**
 * Possibly translates database exceptions into, a more clear,
 * constraint violation exception.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class ConstraintViolationExceptionTranslator {
    private ConstraintViolationResolver violationResolver;
    private ConstraintViolationExceptionFactory exceptionFactory;

    /**
     * Construct a new {@link ConstraintViolationExceptionTranslator}.
     * @param violationResolver resolves the constraint violation from an exception
     */
    public ConstraintViolationExceptionTranslator(ConstraintViolationResolver violationResolver) {
        this(violationResolver, new SimpleConstraintViolationExceptionFactory());
    }

    /**
     * Construct a new {@link ConstraintViolationExceptionTranslator}.
     * @param violationResolver resolves the constraint violation from an exception
     * @param exceptionFactory creates an exception for some constraint violation
     */
    public ConstraintViolationExceptionTranslator(ConstraintViolationResolver violationResolver, ConstraintViolationExceptionFactory exceptionFactory) {
        Assert.notNull(violationResolver, "Property 'violation resolver' cannot be null");
        Assert.notNull(exceptionFactory, "Property 'exception factory' cannot be null");
        this.violationResolver = violationResolver;
        this.exceptionFactory = exceptionFactory;
    }

    /**
     * Attempt to translate an exception into a constraint violation exception.
     * @return a constraint violation exception, or {@code null} if no translation could be done
     */
    public Throwable translateExceptionIfPossible(Throwable throwable) {
        Throwable result = null;
        ConstraintViolation violation = violationResolver.resolve(throwable);
        if (violation != null) {
            result = exceptionFactory.createException(violation);
        }
        return result;
    }

}
