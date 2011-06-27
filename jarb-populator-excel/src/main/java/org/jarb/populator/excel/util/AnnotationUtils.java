package org.jarb.populator.excel.util;

import java.lang.annotation.Annotation;

public class AnnotationUtils {

    public static boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationClass) {
        return beanClass.getAnnotation(annotationClass) != null;
    }
    
}
