package org.jarbframework.constraint.violation.resolver;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Looks at the message of our root cause to determine the constraint violation.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public class RootCauseMessageViolationResolver implements DatabaseConstraintViolationResolver {

    private final ViolationMessageResolver messageViolationResolver;
    
    public RootCauseMessageViolationResolver(ViolationMessageResolver messageViolationResolver) {
        this.messageViolationResolver = messageViolationResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;
        
        String rootCauseMessage = getRootCauseMessage(throwable);
        if (isNotBlank(rootCauseMessage)) {
            violation = messageViolationResolver.resolveByMessage(rootCauseMessage);
        }
        
        return violation;
    }

    private String getRootCauseMessage(Throwable throwable) {
        return ExceptionFinder.getRootCause(throwable).getMessage();
    }

}
