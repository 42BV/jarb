package org.jarbframework.constraint.violation.resolver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Matches the exception message on a database constraint violation pattern.
 * Whenever the pattern matches we build a new violation, using the variables.
 * 
 * @author Jeroen van Schagen
 */
public class PatternViolationResolver extends RootCauseMessageViolationResolver {

    private final List<ViolationPattern> patterns = new LinkedList<ViolationPattern>();

    @Override
    public final DatabaseConstraintViolation resolve(String message) {
    	DatabaseConstraintViolation violation = null;
        for (ViolationPattern pattern : patterns) {
            violation = pattern.match(message);
            if (violation != null) {
                break;
            }
        }
        return violation;
    }
    
    /**
     * Register a new vendor specific message pattern.
     * @param regex the regular expression that matches our message
     * @param builder the builder of our violation
     */
    protected void registerPattern(String regex, ViolationBuilder builder) {
        patterns.add(new ViolationPattern(regex, builder));
    }
    
    /**
     * Creates a new database constraint violation.
     */
    protected interface ViolationBuilder {
        
        /**
         * Build a database constraint violation.
         * @param variables the variables in our regular expression
         * @return the created violation
         */
        DatabaseConstraintViolation build(VariableAccessor variables);

    }
    
    /**
     * Accessor class to hide our regex matcher from the mapper.
     */
    protected static final class VariableAccessor {
        
        private final Matcher matcher;
        
        private VariableAccessor(Matcher matcher) {
            this.matcher = matcher;
        }
        
        /**
         * Retrieve the value of a variable.
         * @param number the variable number
         * @return the variable value
         */
        public String get(int number) {
            return matcher.group(number);
        }
        
    }
    
    /**
     * Violation pattern that can be registered.
     */
    private static class ViolationPattern {
        
        /**
         * Pattern that our message should match.
         */
        private final Pattern pattern;
        
        /**
         * Builder used to create a new violation based on our message.
         */
        private final ViolationBuilder builder;
        
        private ViolationPattern(String regex, ViolationBuilder builder) {
            this.pattern = Pattern.compile(regex);
            this.builder = builder;
        }
        
        /**
         * Attempt to create a constraint violation based on a message.
         * @param message the violation message
         * @return the violation, or {@code null} when it could not be matched
         */
        public DatabaseConstraintViolation match(String message) {
            DatabaseConstraintViolation violation = null;
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                violation = builder.build(new VariableAccessor(matcher));
            }
            return violation;
        }
        
    }

}
