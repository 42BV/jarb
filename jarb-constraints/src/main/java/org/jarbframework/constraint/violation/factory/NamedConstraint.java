package org.jarbframework.constraint.violation.factory;

import static org.jarbframework.constraint.violation.factory.NameMatchingStrategy.*;

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
public @interface NamedConstraint {

    /**
     * Name of the constraint <b>required</b>.
     * @return constraint name
     */
    String[] value();

    /**
     * Strategy used to match the name.
     * @return matching strategy
     */
    NameMatchingStrategy strategy() default EXACT_IGNORE_CASE;
    
}
