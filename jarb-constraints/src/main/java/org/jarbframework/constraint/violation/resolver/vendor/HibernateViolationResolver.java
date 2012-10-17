package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.DatabaseConstraintViolationResolver;

/**
 * Constraint violation resolver that (only) retrieves the constraint name from our hibernate exception.
 * @see org.hibernate.exception.ConstraintViolationException
 * @author Jeroen van Schagen
 * @since 17-10-2012
 */
public class HibernateViolationResolver implements DatabaseConstraintViolationResolver {

    private static final String CONSTRAINT_EXCEPTION_CLASS_NAME = "org.hibernate.exception.ConstraintViolationException";
    
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;
        
        // Compare on the class name initially, as hibernate could not be on the classpath
        if(CONSTRAINT_EXCEPTION_CLASS_NAME.equals(throwable.getClass().getName())) {
            String constraintName = ((org.hibernate.exception.ConstraintViolationException) throwable).getConstraintName();
            violation = new DatabaseConstraintViolation(constraintName);
        }
        
        return violation;
    }

}
