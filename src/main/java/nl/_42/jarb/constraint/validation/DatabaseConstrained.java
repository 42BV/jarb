package nl._42.jarb.constraint.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ TYPE, FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DatabaseConstrainedAdapter.class)
public @interface DatabaseConstrained {

    /**
     * Reference to the entity class that contains this annotated object. This is only
     * needed when the annotated object is not an entity itself.
     * <br><br>
     * For example: {@code Person.class} when validating the address of a person.
     * @return reference to the entity class
     */
    Class<?> entityClass() default Object.class;

    /**
     * Path to the annotated object, from our declared entity class. This is only
     * needed when the annotated object is not an entity itself.
     * <br><br>
     * For example: {@code address} when validating the address of a person.
     * @return path to the annotated object
     */
    String propertyName() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "";

}
