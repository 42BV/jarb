/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.types;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to declare a property as a custom type, for example a percentage.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
@Documented
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface PropertyType {
    
    String value() default "";

}
