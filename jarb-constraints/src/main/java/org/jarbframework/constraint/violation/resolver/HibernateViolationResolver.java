package org.jarbframework.constraint.violation.resolver;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String EXCEPTION_CLASS_NAME = "org.hibernate.exception.ConstraintViolationException";
    private static final boolean EXCEPTION_ON_CLASSPATH;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateViolationResolver.class);
    
    /*
     * Determine if the desired exception class is on our classpath once,
     * by storing it in a static value on construction of our class.
     */
    static {
        EXCEPTION_ON_CLASSPATH = isExceptionOnClasspath();
    }
    
    private static boolean isExceptionOnClasspath() {
        boolean exceptionOnClasspath = false;
        try {
            Class.forName(EXCEPTION_CLASS_NAME);
            exceptionOnClasspath = true;
        } catch(ClassNotFoundException e) {
            LOGGER.debug("Cannot use this resolver as the class '{}' is not on our classpath.", EXCEPTION_CLASS_NAME);
        }
        return exceptionOnClasspath;
    }
    
    @Override
    public DatabaseConstraintViolation resolve(Throwable throwable) {
        DatabaseConstraintViolation violation = null;
        
        if(EXCEPTION_ON_CLASSPATH && throwable instanceof org.hibernate.exception.ConstraintViolationException) {
            String constraintName = ((org.hibernate.exception.ConstraintViolationException) throwable).getConstraintName();
            violation = new DatabaseConstraintViolation(constraintName);
        }
        
        return violation;
    }

}
