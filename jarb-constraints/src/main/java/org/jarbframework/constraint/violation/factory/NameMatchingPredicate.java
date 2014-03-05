package org.jarbframework.constraint.violation.factory;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.StringUtils;

/**
 * Matches constraint violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public class NameMatchingPredicate implements ViolationPredicate {
    
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
    public boolean isSupported(DatabaseConstraintViolation violation) {
    	boolean matches = false;
    	
        String actualName = violation.getConstraintName();
        if (StringUtils.isNotBlank(actualName)) {
        	matches = matchingStrategy.matches(expectedName, actualName);
        }
        
        return matches;
    }

}
