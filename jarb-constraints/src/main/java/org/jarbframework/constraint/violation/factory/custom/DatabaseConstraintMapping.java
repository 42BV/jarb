package org.jarbframework.constraint.violation.factory.custom;

import static org.jarbframework.constraint.violation.factory.custom.NameMatchingStrategy.EXACT_IGNORE_CASE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an exception to specific database constraint(s).
 * @author Jeroen van Schagen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseConstraintMapping {

    /**
     * Name of the constraint {@code required}.
     * @return constraint name
     */
    String value();
    
    /**
     * Strategy used to match the name.
     * @return matching strategy
     */
    NameMatchingStrategy strategy() default EXACT_IGNORE_CASE;
    
}
