package org.jarbframework.constraint.violation.resolver;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks at the message of our root cause to determine the constraint violation.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class RootCauseMessageViolationResolver implements DatabaseConstraintViolationResolver {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Determines the constraint violation based on a message.
     */
    private final MessageBasedViolationResolver messageResolver;
    
    /**
     * Construct a new root cause message violation resolver.
     * @param messageResolver the resolver of our root cause message
     */
    public RootCauseMessageViolationResolver(MessageBasedViolationResolver messageResolver) {
        this.messageResolver = messageResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;
        
        String rootCauseMessage = getRootCauseMessage(throwable);
        if (isNotBlank(rootCauseMessage)) {
            logger.debug("Attempting to resolve violation based on message: {}", rootCauseMessage);
            violation = messageResolver.resolve(rootCauseMessage);
        }
        
        return violation;
    }

    private String getRootCauseMessage(Throwable throwable) {
        return ExceptionFinder.getRootCause(throwable).getMessage();
    }

}
