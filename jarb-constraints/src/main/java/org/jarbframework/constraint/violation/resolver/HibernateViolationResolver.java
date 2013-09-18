package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.Classes;

/**
 * Constraint violation resolver that retrieves the constraint name
 * from our Hibernate exception. Note that this resolver will only
 * be used when Hibernate is on the classpath.
 * 
 * @see org.hibernate.exception.ConstraintViolationException
 * @author Jeroen van Schagen
 * @since 17-10-2012
 */
public class HibernateViolationResolver implements DatabaseConstraintViolationResolver {

    private static final boolean HIBERNATE_ON_CLASSPATH;
        
    static {
        HIBERNATE_ON_CLASSPATH = Classes.isOnClasspath("org.hibernate.exception.ConstraintViolationException");
    }
    
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;

        if (HIBERNATE_ON_CLASSPATH) {
            if (throwable instanceof org.hibernate.exception.ConstraintViolationException) {
                String constraintName = ((org.hibernate.exception.ConstraintViolationException) throwable).getConstraintName();
                violation = new DatabaseConstraintViolation(constraintName);
            }
        }
        
        return violation;
    }

}
