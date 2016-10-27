package org.jarbframework.constraint.violation.factory;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;
import org.jarbframework.utils.StringUtils;

import java.util.Arrays;

/**
 * Matches constraint violations based on their constraint name.
 * @author Jeroen van Schagen
 * @since Sep 2, 2011
 */
public class NameMatchingPredicate implements ViolationPredicate {
    
	/** The expected constraint name. */
    private final String[] expectedNames;
    
    /** The matching strategy used to compare constraint names. */
    private final NameMatchingStrategy matchingStrategy;
    
    /**
     * Construct a new predicate.
     * @param constraintNames the expected constraint name
     * @param matchingStrategy the matching strategy to use
     */
    public NameMatchingPredicate(String[] constraintNames, NameMatchingStrategy matchingStrategy) {
        this.expectedNames = constraintNames;
        this.matchingStrategy = matchingStrategy;
    }

    @Override
    public boolean isSupported(DatabaseConstraintViolation violation) {
    	boolean matches = false;
    	
        String actualName = violation.getConstraintName();
        if (StringUtils.isNotBlank(actualName)) {
            matches = Arrays.stream(expectedNames).anyMatch(expected -> expected.matches(actualName));
        }
        
        return matches;
    }

}
