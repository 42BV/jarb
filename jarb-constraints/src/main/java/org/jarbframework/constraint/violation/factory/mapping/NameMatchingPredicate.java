/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.violation.factory.mapping;

import org.apache.commons.lang3.StringUtils;
import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

import com.google.common.base.Predicate;

/**
 * Matches constraint violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public class NameMatchingPredicate implements Predicate<DatabaseConstraintViolation> {
    
	/** The expected constraint name. */
    private final String expectedName;
    
    /** The matching strategy used to compare constraint names. */
    private final NameMatchingStrategy matchingStrategy;
    
    /**
     * Construct a new predicate.
     * @param constraintName the expected constraint name
     * @param matchingStrategy the matching strategy to use
     */
    public NameMatchingPredicate(String constraintName, NameMatchingStrategy matchingStrategy) {
        this.expectedName = constraintName;
        this.matchingStrategy = matchingStrategy;
    }

    @Override
    public boolean apply(DatabaseConstraintViolation violation) {
    	boolean matches = false;
    	
        String actualName = violation.getConstraintName();
        if (StringUtils.isNotBlank(actualName)) {
        	matches = matchingStrategy.matches(expectedName, actualName);
        }
        
        return matches;
    }

}
