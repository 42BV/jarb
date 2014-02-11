/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

/**
 * Makes the application aware of database constaints.  
 *
 * @author jeroen
 * @since Feb 11, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(EnableDatabaseConstraintsConfiguration.class)
public @interface EnableDatabaseConstraints {

    /**
     * The base package that should be scanned.
     * 
     * @return the base package
     */
    String basePackage() default "";
    
    /**
     * The reference to our entity manager factory.
     * 
     * @return the entity manager factory name
     */
    String entityManagerFactory() default "entityManagerFactory";
    
    /**
     * The annotation that should perform the exception translations.
     * By default we take the {@link Repository} class.
     * 
     * @return the annotation that performs exception translations
     */
    Class<? extends Annotation> translatingAnnotation() default Repository.class;

}
