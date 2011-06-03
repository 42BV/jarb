package org.jarb.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DatabaseConstraintValidator.class)
public @interface DatabaseConstraint {
    /**
     * Identifier of the constraint repository that should be used.
     * Whenever left empty, we assume that there is only one constraint
     * repository in our application context.
     */
    String constraintRepository() default "";

    // Default validation attributes

    String message() default "{nl.mad.constraint.validation.DatabaseConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
