package nl._42.jarb.constraint.violation.resolver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl._42.jarb.constraint.violation.DatabaseConstraintViolation;

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
     * @param pattern the pattern to register
     */
    protected void register(ViolationPattern pattern) {
        patterns.add(pattern);
    }
    
    /**
     * Creates a new database constraint violation.
     */
    public static abstract class ViolationPattern {
        
        /**
         * Pattern that our message should match.
         */
        private final Pattern pattern;
        
        public ViolationPattern(String regex) {
            this.pattern = Pattern.compile(regex);
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
                violation = build(new VariableAccessor(matcher));
            }
            return violation;
        }
        
        /**
         * Build a database constraint violation.
         * @param variables the variables in our regular expression
         * @return the created violation
         */
        public abstract DatabaseConstraintViolation build(VariableAccessor variables);

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

}
