package org.jarb.violation;

import org.jarb.violation.factory.ConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.ConstraintViolationResolver;

/**
 * Possibly translates database exceptions into, a more clear,
 * constraint violation exception.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class ConstraintViolationExceptionTranslator {
    private ConstraintViolationExceptionFactory exceptionFactory;
    private ConstraintViolationResolver violationResolver;

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

    public void setExceptionFactory(ConstraintViolationExceptionFactory exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public void setViolationResolver(ConstraintViolationResolver violationResolver) {
        this.violationResolver = violationResolver;
    }

}
