/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationUtils {

    public static boolean hasAnnotation(Field field, Class<? extends Annotation> annotationType) {
        return field.getAnnotation(annotationType) != null;
    }

    public static boolean hasAnnotation(Method method, Class<? extends Annotation> annotationType) {
        return method.getAnnotation(annotationType) != null;
    }

}
