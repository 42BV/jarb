package nl._42.jarb.constraint.violation.resolver;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;
import nl._42.jarb.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks at the message of our root cause to determine the constraint violation.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public abstract class RootCauseMessageViolationResolver implements DatabaseConstraintViolationResolver {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public final DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;
        
        String message = getRootCause(throwable).getMessage();
        if (StringUtils.isNotBlank(message)) {
            logger.debug("Resolving violation based on message: {}", message);
            violation = resolve(message);
        }
        
        return violation;
    }

    /**
     * Resolve the database constraint violation based on a message.
     * @param message the root cause message
     * @return the resolved violation, if any
     */
    public abstract DatabaseConstraintViolation resolve(String message);

    /**
     * Retrieve the initial throwable cause, furthest down inside the stack.
     * @param throwable starting throwable
     * @return initial throwable
     */
    private static Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

}
