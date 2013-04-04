package org.jarbframework.constraint.violation.resolver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jarbframework.constraint.violation.DatabaseConstraintViolation;

/**
 * Regex based message resolver.
 * @author Jeroen van Schagen
 * @since 04-03-2012
 */
public class RegexViolationResolver implements MessageBasedViolationResolver {
    
    /**
     * All registered violation pattern.
     */
    private final List<ViolationPattern> violationPatterns = new LinkedList<ViolationPattern>();

    /**
     * Register a new violation message pattern.
     * @param regex a regex based description of our exception message
     * @param builder converts our message into a constraint violaton
     */
    public void registerPattern(String regex, DatabaseConstraintViolationBuilder builder) {
        violationPatterns.add(new ViolationPattern(regex, builder));
    }
    
    @Override
    public final DatabaseConstraintViolation resolve(String message) {
        DatabaseConstraintViolation violation = null;
        for (ViolationPattern violationPattern : violationPatterns) {
            violation = violationPattern.match(message);
            if (violation != null) {
                break;
            }
        }
        return violation;
    }
    
    /**
     * Creates a new database constraint violation.
     */
    protected interface DatabaseConstraintViolationBuilder {
        
        /**
         * Build a database constraint violation.
         * @param variables the variables in our regex
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
    private static final class ViolationPattern {
        
        /**
         * Pattern that our message should match.
         */
        private final Pattern pattern;
        
        /**
         * Builder used to create a new violation based on our message.
         */
        private final DatabaseConstraintViolationBuilder builder;
        
        private ViolationPattern(String regex, DatabaseConstraintViolationBuilder builder) {
            this.pattern = Pattern.compile(regex);
            this.builder = builder;
        }
        
        /**
         * Attempt to create a constraint violation based on a message.
         * @param message the violation message
         * @return the violation, or {@code null} when it could not be matche
         */
        public DatabaseConstraintViolation match(String message) {
            DatabaseConstraintViolation violation = null;
            Matcher matcher = pattern.matcher(message);
            if(matcher.matches()) {
                VariableAccessor variables = new VariableAccessor(matcher);
                violation = builder.build(variables);
            }
            return violation;
        }
        
    }
    
}
