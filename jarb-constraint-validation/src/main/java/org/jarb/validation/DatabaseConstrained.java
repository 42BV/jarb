package org.jarb.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.jarbframework.constraint.AutoIncremental;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DatabaseConstrainedValidator.class)
public @interface DatabaseConstrained {
    String message() default "{org.jarb.validation.DatabaseConstraint.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Class used to state that a property is auto incremental
     * in the database.
     */
    Class<? extends Annotation> autoIncrementalClass() default AutoIncremental.class;

    /**
     * Component identifier of the validator factory to be used.
     * Note that this can only be left empty whenever there is one
     * factory in the application context.
     */
    String factory() default "";

    /**
     * Component identifier of the database constraint repository.
     * Note this can only be left empty whenever there is one
     * repository in the application context.
     */
    String repository() default "";

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DatabaseConstrained[] value();
    }
}
