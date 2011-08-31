package org.jarb.violation;

import org.jarb.violation.factory.ConstraintViolationExceptionFactory;
import org.jarb.violation.factory.SimpleConstraintViolationExceptionFactory;
import org.jarb.violation.resolver.ConstraintViolationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Possibly translates database exceptions into, a more clear,
 * constraint violation exception. Whenever no translation could
 * be performed, we return the original exception.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class ConstraintViolationExceptionTranslator {
    private final Logger logger = LoggerFactory.getLogger(ConstraintViolationExceptionTranslator.class);

    /** Resolves the constraint violation from an exception. **/
    private final ConstraintViolationResolver violationResolver;
    /** Creates an exception for some constraint violation. **/
    private final ConstraintViolationExceptionFactory exceptionFactory;

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
        Throwable translatedException = null;
        DatabaseConstraintViolation violation = violationResolver.resolve(throwable);
        if (violation != null) {
            translatedException = exceptionFactory.createException(violation, throwable);
            logger.info("Translated '{}' into '{}'.", throwable.getClass().getSimpleName(), translatedException.getClass().getSimpleName());
        }
        return translatedException;
    }

}
