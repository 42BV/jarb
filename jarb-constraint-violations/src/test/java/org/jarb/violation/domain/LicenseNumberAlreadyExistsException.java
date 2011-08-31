package org.jarb.violation.domain;

import org.jarb.violation.DatabaseConstraintViolation;
import org.jarb.violation.UniqueKeyViolationException;
import org.jarb.violation.factory.ConstraintViolationExceptionFactory;

/**
 * Car license number can only be used once.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LicenseNumberAlreadyExistsException extends UniqueKeyViolationException {
    private static final long serialVersionUID = 8122213238219945641L;
    private ConstraintViolationExceptionFactory factory;
    
    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation) {
        super(violation);
    }

    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause) {
        super(violation, cause);
    }

    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause, ConstraintViolationExceptionFactory factory) {
        super(violation, cause);
        this.factory = factory;
    }

    /**
     * Retrieve the factory that generated this exception.
     * @return exception factory
     */
    public ConstraintViolationExceptionFactory getExceptionFactory() {
        return factory;
    }

}
