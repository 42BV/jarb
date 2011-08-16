/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * State that a field value is auto incremental in the database.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
@Documented
@Target(FIELD)
@Retention(RUNTIME)
public @interface AutoIncremental {

}
