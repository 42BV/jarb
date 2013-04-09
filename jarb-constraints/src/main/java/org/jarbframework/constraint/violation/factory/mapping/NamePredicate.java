/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.violation.factory.mapping;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

import com.google.common.base.Predicate;

/**
 * Matches violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public class NamePredicate implements Predicate<DatabaseConstraintViolation> {
    
    private final String expectedName;
    
    private final NameMatchingStrategy matchingStrategy;
    
    public NamePredicate(String constraintName, NameMatchingStrategy matchingStrategy) {
        this.expectedName = constraintName;
        this.matchingStrategy = matchingStrategy;
    }

    @Override
    public boolean apply(DatabaseConstraintViolation violation) {
        String actualName = violation.getConstraintName();
        return matchingStrategy.matches(expectedName, actualName);
    }

}
