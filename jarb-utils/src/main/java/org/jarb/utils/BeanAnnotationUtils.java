/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import static org.jarb.utils.ReflectionUtils.getFieldNames;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Facilitates the usage of annotations on bean properties. 
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class BeanAnnotationUtils {

    /**
     * Returns the bean's property annotation of the specified type, whenever
     * available. At first we will look at the bean property field, if present,
     * and otherwise the getter method.
     * @param beanClass bean class
     * @param propertyName property name
     * @param annotationType type of annotation
     * @return the annotation desired, whenever present
     */
    public static <T extends Annotation> T getAnnotation(Class<?> beanClass, String propertyName, Class<T> annotationType) {
        return getAnnotation(beanClass, propertyName, annotationType, true, false);
    }

    public static <T extends Annotation> T getAnnotation(Class<?> beanClass, String propertyName, Class<T> annotationType, boolean includeGetter,
            boolean includeSetter) {
        T annotation = null;
        Field field = ReflectionUtils.findField(beanClass, propertyName);
        if (field != null) {
            annotation = field.getAnnotation(annotationType);
        }
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
        if (propertyDescriptor != null) {
            if (annotation == null && includeGetter) {
                annotation = getAnnotationFromGetter(propertyDescriptor, annotationType);
            }
            if (annotation == null && includeSetter) {
                annotation = getAnnotationFromSetter(propertyDescriptor, annotationType);
            }
        }
        return annotation;
    }

    private static <T extends Annotation> T getAnnotationFromGetter(PropertyDescriptor propertyDescriptor, Class<T> annotationType) {
        T annotation = null;
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
            annotation = readMethod.getAnnotation(annotationType);
        }
        return annotation;
    }

    private static <T extends Annotation> T getAnnotationFromSetter(PropertyDescriptor propertyDescriptor, Class<T> annotationType) {
        T annotation = null;
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
            annotation = writeMethod.getAnnotation(annotationType);
        }
        return annotation;
    }

    public static boolean hasAnnotation(Class<?> beanClass, String propertyName, Class<? extends Annotation> annotationType) {
        return getAnnotation(beanClass, propertyName, annotationType) != null;
    }

    public static boolean hasAnnotation(Class<?> beanClass, String propertyName, Class<? extends Annotation> annotationType, boolean includeGetter,
            boolean includeSetter) {
        return getAnnotation(beanClass, propertyName, annotationType, includeGetter, includeSetter) != null;
    }

    public static String findPropertyWithAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        for (String fieldName : getFieldNames(beanClass)) {
            if (hasAnnotation(beanClass, fieldName, Id.class)) {
                return fieldName;
            }
        }
        throw new IllegalStateException("Bean '" + beanClass + "' does not have a property annotated as @" + annotationType.getClass().getSimpleName() + ".");
    }

}
