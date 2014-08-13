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
 * Makes the application aware of database constaints:
 * <p>
 * <ul>
 *  <li>Enabling bean validation based on the database schema.</li>
 *  <li>Translating JDBC exceptions into clear database violations.</li>
 * </ul>
 *
 * @author Jeroen van Schagen
 * @since Feb 11, 2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(DatabaseConstraintsConfiguration.class)
public @interface EnableDatabaseConstraints {

    /**
     * The base packages that should be scanned. Use {@link #basePackageClasses()}
     * for a type-safe alternative to String-based package names.
     * 
     * @return the base package
     */
    String[] basePackages() default {};
    
    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan
     * for annotated components. The package of each class specified will be scanned.
     * 
     * @return the base classes
     */
    Class<?>[] basePackageClasses() default {};
    
    /**
     * The reference to our entity manager factory.
     * 
     * @return the entity manager factory name
     */
    String entityManagerFactory() default "entityManagerFactory";
    
    /**
     * The reference to our data source.
     * 
     * @return the data source
     */
    String dataSource() default "dataSource";
    
    /**
     * The bean annotations that we should proxy to translate exceptions.
     * By default we proxy the {@link Repository} beans.
     * 
     * @return the annotation that performs exception translations
     */
    Class<? extends Annotation> proxyAnnotation() default Repository.class;

}
