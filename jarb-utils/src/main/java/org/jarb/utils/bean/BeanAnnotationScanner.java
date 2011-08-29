/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.bean;

import java.lang.annotation.Annotation;

/**
 * Scans for annotations in beans.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public interface BeanAnnotationScanner {

    <T extends Annotation> T findAnnotation(Class<?> beanClass, Class<T> annotationType);

    boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType);

    <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType);

    boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType);

}
