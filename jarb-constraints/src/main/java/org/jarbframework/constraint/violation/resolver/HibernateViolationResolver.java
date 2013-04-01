package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.Classes;

/**
 * Constraint violation resolver that (only) retrieves the constraint name from our hibernate exception.
 * Note that this resolver will only work when hibernate is on the classpath, but does not result
 * in runtime exception when it is not on the classpath.
 * 
 * @see org.hibernate.exception.ConstraintViolationException
 * @author Jeroen van Schagen
 * @since 17-10-2012
 */
public class HibernateViolationResolver implements DatabaseConstraintViolationResolver {

    private static final boolean EXCEPTION_ON_CLASSPATH;
        
    static {
        EXCEPTION_ON_CLASSPATH = Classes.isOnClasspath("org.hibernate.exception.ConstraintViolationException");
    }
    
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;

        if(EXCEPTION_ON_CLASSPATH) {
            if(throwable instanceof org.hibernate.exception.ConstraintViolationException) {
                String constraintName = ((org.hibernate.exception.ConstraintViolationException) throwable).getConstraintName();
                violation = new DatabaseConstraintViolation(constraintName);
            }
        }
        
        return violation;
    }

}
