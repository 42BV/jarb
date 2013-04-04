package org.jarbframework.constraint.violation.factory.mapping;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.factory.DatabaseConstraintExceptionFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * Describes the mapping of an exception factory on constraint violations.
 * @author Jeroen van Schagen
 * @since 17-09-2011
 */
public class ExceptionFactoryMapping {
    
    private final Predicate<DatabaseConstraintViolation> violationPredicate;
    
    private final DatabaseConstraintExceptionFactory exceptionFactory;

    public ExceptionFactoryMapping(Predicate<DatabaseConstraintViolation> violationPredicate, DatabaseConstraintExceptionFactory exceptionFactory) {
        this.violationPredicate = Preconditions.checkNotNull(violationPredicate, "Violation predicate cannot be null.");
        this.exceptionFactory = Preconditions.checkNotNull(exceptionFactory, "Exception factory cannot be null.");
    }

    public boolean isSupported(DatabaseConstraintViolation violation) {
        return violationPredicate.apply(violation);
    }

    public DatabaseConstraintExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }
    
}
