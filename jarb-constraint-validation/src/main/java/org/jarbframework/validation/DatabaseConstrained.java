package org.jarbframework.validation;

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
@Constraint(validatedBy = DatabaseConstraintValidatorAdapter.class)
public @interface DatabaseConstrained {
    /**
     * Identifier of the {@link DatabaseConstraintValidator} bean that should be used
     * for constraint validation. When left blank we attempt to autowire the validator
     * from our context, or build a new default validator instance.
     */
    String id() default "";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "";
}
