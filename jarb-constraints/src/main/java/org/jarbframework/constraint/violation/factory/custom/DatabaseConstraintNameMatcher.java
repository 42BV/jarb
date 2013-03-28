/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.violation.factory.custom;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Matches violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public class DatabaseConstraintNameMatcher implements DatabaseConstraintViolationMatcher {
    
    private final String expectedConstraintName;
    
    private final NameMatchingStrategy nameMatchingStrategy;
    
    public DatabaseConstraintNameMatcher(String constraintName, NameMatchingStrategy nameMatchingStrategy) {
        this.expectedConstraintName = constraintName;
        this.nameMatchingStrategy = nameMatchingStrategy;
    }

    @Override
    public boolean matches(DatabaseConstraintViolation violation) {
        String actualConstraintName = violation.getConstraintName();
        return nameMatchingStrategy.matches(expectedConstraintName, actualConstraintName);
    }

}
