package org.jarb.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
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
@Constraint(validatedBy = DatabaseConstrainedValidator.class)
public @interface DatabaseConstrained {
    String message() default "{nl.mad.constraint.validation.DatabaseConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    /**
     * Identifier of the validator factory that should be used.
     * Can only be left empty is there is one validator factory
     * in the application context.
     */
    String factory() default "";
    
    /**
     * Identifier of the constraint repository that should be used.
     * Can only be left empty is there is one constraint repository
     * in the application context.
     */
    String columnMetadataRepository() default "";
    
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DatabaseConstrained[] value();
    }
}
