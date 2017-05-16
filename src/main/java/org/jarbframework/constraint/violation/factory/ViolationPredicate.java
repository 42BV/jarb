/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.violation.factory;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Determine if a violation is supported for a factory.
 *
 * @author Jeroen van Schagen
 * @since Mar 5, 2014
 */
public interface ViolationPredicate {
    
    boolean isSupported(DatabaseConstraintViolation violation);

}
