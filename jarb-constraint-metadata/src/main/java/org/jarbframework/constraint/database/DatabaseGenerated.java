/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.database;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * State that a property value is auto incremental.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
@Documented
@Target({FIELD,METHOD})
@Retention(RUNTIME)
public @interface DatabaseGenerated {

}
