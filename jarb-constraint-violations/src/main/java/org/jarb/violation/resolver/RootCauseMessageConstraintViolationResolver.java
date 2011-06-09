package org.jarb.violation.resolver;

import org.apache.commons.lang.StringUtils;
import org.jarb.violation.ConstraintViolation;

/**
 * Looks at the message of our root cause to determine the constraint violation.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public abstract class RootCauseMessageConstraintViolationResolver implements ConstraintViolationResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public final ConstraintViolation resolve(Throwable throwable) {
        ConstraintViolation violation = null;
        String rootMessage = ExceptionFinder.getRootCause(throwable).getMessage();
        if(StringUtils.isNotBlank(rootMessage)) {
            violation = resolveByMessage(rootMessage);
        }
        return violation;
    }

    /**
     * Resolve the constraint violation based on our root cause message.
     * @param message root cause exception message
     * @return constraint violation
     */
    protected abstract ConstraintViolation resolveByMessage(String message);

}
