/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

public class AnnotationUtils {

    public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }

    public static boolean hasAnnotation(Object bean, String propertyName, Class<? extends Annotation> annotationClass) {
        return hasAnnotation(ReflectionUtils.findField(bean.getClass(), propertyName), annotationClass);
    }

}
