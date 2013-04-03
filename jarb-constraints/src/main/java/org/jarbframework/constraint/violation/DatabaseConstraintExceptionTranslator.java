package org.jarbframework.constraint.violation;

import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import org.jarbframework.constraint.violation.factory.TypeBasedConstraintExceptionFactory;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Possibly translates database exceptions into, a more clear,
 * constraint violation exception. Whenever no translation could
 * be performed, we return the original exception.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class DatabaseConstraintExceptionTranslator {
	
    private final Logger logger = LoggerFactory.getLogger(DatabaseConstraintExceptionTranslator.class);

    /** Resolves the constraint violation from an exception. **/
    private final DatabaseConstraintViolationResolver violationResolver;
    
    /** Creates an exception for some constraint violation. **/
    private final DatabaseConstraintExceptionFactory exceptionFactory;

    /**
     * Construct a new {@link DatabaseConstraintExceptionTranslator}.
     * @param resolver resolves the constraint violation from an exception
     */
    public DatabaseConstraintExceptionTranslator(DatabaseConstraintViolationResolver resolver) {
        this(resolver, new TypeBasedConstraintExceptionFactory());
    }

    /**
     * Construct a new {@link DatabaseConstraintExceptionTranslator}.
     * @param resolver resolves the constraint violation from an exception
     * @param exceptionFactory creates an exception for some constraint violation
     */
    public DatabaseConstraintExceptionTranslator(DatabaseConstraintViolationResolver resolver, DatabaseConstraintExceptionFactory exceptionFactory) {
        this.violationResolver = notNull(resolver, "Violation resolver cannot be null");
        this.exceptionFactory = notNull(exceptionFactory, "Exception factory cannot be null");
    }

    /**
     * Attempt to translate an exception into a constraint violation exception.
     * @return a constraint violation exception, or {@code null} if no translation could be done
     */
    public Throwable translate(Throwable throwable) {
        Throwable translatedException = null;
        DatabaseConstraintViolation violation = violationResolver.resolve(throwable);
        if (violation != null) {
            translatedException = notNull(exceptionFactory.createException(violation, throwable), "Could not build an exception for " + violation);
            logger.info("Translated {} into {}", describeThrowable(throwable), describeThrowable(translatedException));
        }
        return translatedException;
    }

    private String describeThrowable(Throwable throwable) {
        return throwable.getClass().getSimpleName();
    }

}
