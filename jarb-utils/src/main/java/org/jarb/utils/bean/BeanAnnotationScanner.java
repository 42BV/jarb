/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.bean;

import java.lang.annotation.Annotation;

/**
 * Capable of finding annotations on class- and property declarations.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public interface BeanAnnotationScanner {

    /**
     * Find a specific annotation on the class declaration.
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @return desired annotation, as declared on the class, if any
     */
    <T extends Annotation> T findAnnotation(Class<?> beanClass, Class<T> annotationType);

    /**
     * Check if a specific annotation is declared on the class.
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationTypes);

    /**
     * Find a specific annotation on the property declaration.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @return desired annotation, as declared on the property, if any
     */
    <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType);

    /**
     * Check if a specific property is declared on the property.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType);

}
