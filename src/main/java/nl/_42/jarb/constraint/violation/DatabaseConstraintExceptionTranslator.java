package nl._42.jarb.constraint.violation;

import nl._42.jarb.constraint.violation.factory.DatabaseConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.factory.DefaultConstraintExceptionFactory;
import nl._42.jarb.constraint.violation.resolver.DatabaseConstraintViolationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl._42.jarb.utils.Asserts.notNull;

/**
 * Possibly translates database exceptions into, a more clear,
 * constraint violation exception. Whenever no translation could
 * be performed, we return the original exception.
 * 
 * @author Jeroen van Schagen
 * @since 17-05-2011
 */
public class DatabaseConstraintExceptionTranslator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** Resolves the constraint violation from an exception. **/
    private final DatabaseConstraintViolationResolver violationResolver;
    
    /** Creates an exception for some constraint violation. **/
    private final DatabaseConstraintExceptionFactory exceptionFactory;

    /**
     * Construct a new {@link DatabaseConstraintExceptionTranslator}.
     * @param violationResolver resolves the constraint violation from an exception
     */
    public DatabaseConstraintExceptionTranslator(DatabaseConstraintViolationResolver violationResolver) {
        this(violationResolver, new DefaultConstraintExceptionFactory());
    }

    /**
     * Construct a new {@link DatabaseConstraintExceptionTranslator}.
     * @param violationResolver resolves the constraint violation from an exception
     * @param exceptionFactory creates an exception for some constraint violation
     */
    public DatabaseConstraintExceptionTranslator(DatabaseConstraintViolationResolver violationResolver, DatabaseConstraintExceptionFactory exceptionFactory) {
        this.violationResolver = notNull(violationResolver, "Violation resolver cannot be null.");
        this.exceptionFactory = notNull(exceptionFactory, "Exception factory cannot be null.");
    }

    /**
     * Attempt to translate an exception into a constraint violation exception.
     * @param throwable exception to translate to a JARB-managed exception
     * @return a constraint violation exception, or {@code null} if no translation could be done
     */
    public Throwable translate(Throwable throwable) {
        try {
            Throwable translation = null;
            DatabaseConstraintViolation violation = violationResolver.resolve(throwable);
            if (violation != null) {
                translation = exceptionFactory.buildException(violation, throwable);
            }
            return translation;
        } catch (RuntimeException rte) {
            logger.error("Could not translate exception", rte);
            return null; // Translation failed, use original exception
        }
    }

}
