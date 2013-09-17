package org.jarbframework.constraint.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ TYPE, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DatabaseConstrainedAdapter.class)
public @interface DatabaseConstrained {
	
    /**
     * Identifier of the {@link DatabaseConstraintValidator} bean that should be used
     * for constraint validation. When left blank we attempt to autowire the validator
     * from our context, or build a new default validator instance.
     */
    String id() default "";

    /**
     * Reference to the entity class that contains this annotated object. This is only
     * needed when the annotated object is not an entity itself.
     * <br><br>
     * For example: {@code Person.class} when validating the address of a person.
     */
    Class<?> entityClass() default Object.class;
    
    /**
     * Path to the annotated object, from our declared entity class. This is only
     * needed when the annotated object is not an entity itself.
     * <br><br>
     * For example: {@code address} when validating the address of a person.
     */
    String propertyName() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "";
    
}
