package org.jarbframework.constraint.violation.factory.mapping;

/**
 * Different strategies for matching a name.
 * @author Jeroen van Schagen
 */
public enum NameMatchingStrategy {

    EXACT {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.equals(expected);
        }
        
    }, EXACT_IGNORE_CASE {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.equalsIgnoreCase(expected);
        }
        
    }, STARTS_WITH {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.startsWith(expected);
        }
        
    }, ENDS_WITH {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.endsWith(expected);
        }
        
    }, CONTAINS {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.contains(expected);
        }
        
    }, REGEX {
        
        @Override
        public boolean matches(String expected, String actual) {
            return actual.matches(expected);
        }
        
    };
    
    /**
     * Determine if the provided constraint name matches our expected name.
     * @param expected the expected constraint name
     * @param actual the actual constraint name
     * @return {@code true} whenever the names match, else {@code false}
     */
    public abstract boolean matches(String expected, String actual);
    
}
