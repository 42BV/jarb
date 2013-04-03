package org.jarbframework.constraint.violation.resolver.vendor;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.constraint.violation.resolver.ViolationMessageResolver;

/**
 * H2 based constraint violation resolver.
 * 
 * @author Jeroen van Schagen
 * @since 03-04-2013
 */
public class H2ViolationResolver implements ViolationMessageResolver {

    @Override
    public DatabaseConstraintViolation resolveByMessage(String message) {
        System.out.println(message);
        return null;
    }

}
