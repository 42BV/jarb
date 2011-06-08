package org.jarb.violation.domain;

import org.jarb.violation.ConstraintViolation;
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
    
    public LicenseNumberAlreadyExistsException(ConstraintViolation violation) {
        super(violation);
    }

    // Invoked by reflection based factory as all parameters are supported
    public LicenseNumberAlreadyExistsException(ConstraintViolation violation, Throwable cause) {
        super(violation, cause);
    }

    // Only invoked by custom exception factory
    public LicenseNumberAlreadyExistsException(ConstraintViolation violation, ConstraintViolationExceptionFactory factory) {
        super(violation);
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
