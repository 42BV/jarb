package org.jarbframework.violation.domain;

import org.jarbframework.violation.DatabaseConstraintViolation;
import org.jarbframework.violation.UniqueKeyViolationException;
import org.jarbframework.violation.factory.DatabaseConstraintExceptionFactory;

/**
 * Car license number can only be used once.
 * 
 * @author Jeroen van Schagen
 * @since 27-05-2011
 */
public class LicenseNumberAlreadyExistsException extends UniqueKeyViolationException {
    private static final long serialVersionUID = 8122213238219945641L;
    private DatabaseConstraintExceptionFactory factory;
    
    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation) {
        super(violation);
    }

    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause) {
        super(violation, cause);
    }

    public LicenseNumberAlreadyExistsException(DatabaseConstraintViolation violation, Throwable cause, DatabaseConstraintExceptionFactory factory) {
        super(violation, cause);
        this.factory = factory;
    }

    /**
     * Retrieve the factory that generated this exception.
     * @return exception factory
     */
    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return factory;
    }

}
