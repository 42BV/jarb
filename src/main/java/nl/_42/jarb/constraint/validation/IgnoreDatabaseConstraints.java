package nl._42.jarb.constraint.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Payload;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
public @interface IgnoreDatabaseConstraints {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "";
    
}
