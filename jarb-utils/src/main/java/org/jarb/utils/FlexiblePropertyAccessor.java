/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessor;

/**
 * Attempts to access properties based on the public
 * getter and setters methods. But whenever no matching
 * method could be found we access the field directly.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public class FlexiblePropertyAccessor {
    private final BeanWrapper beanWrapper;
    private final PropertyAccessor fieldAccessor;

    public FlexiblePropertyAccessor(Object bean) {
        this.beanWrapper = new BeanWrapperImpl(bean);
        this.fieldAccessor = new DirectFieldAccessor(bean);
    }

    public Object getPropertyValue(String propertyName) throws BeansException {
        try {
            return beanWrapper.getPropertyValue(propertyName);
        } catch (NotReadablePropertyException e) {
            return fieldAccessor.getPropertyValue(propertyName);
        }
    }

    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        try {
            beanWrapper.setPropertyValue(propertyName, value);
        } catch (NotWritablePropertyException e) {
            fieldAccessor.setPropertyValue(propertyName, value);
        }
    }

    public boolean isReadableProperty(String propertyName) {
        return beanWrapper.isReadableProperty(propertyName) || fieldAccessor.isReadableProperty(propertyName);
    }

    public boolean isWritableProperty(String propertyName) {
        return beanWrapper.isWritableProperty(propertyName) || fieldAccessor.isWritableProperty(propertyName);
    }

    public boolean hasAnnotation(String propertyName, Class<? extends Annotation> annotationType) {
        return hasAnnotation(propertyName, annotationType, true, false);
    }

    public boolean hasAnnotation(String propertyName, Class<? extends Annotation> annotationType, boolean includeGetter, boolean includeSetter) {
        boolean annotationFound = AnnotationUtils.hasAnnotation(fieldAccessor.getPropertyTypeDescriptor(propertyName).getField(), annotationType);
        if (!annotationFound && includeGetter) {
            annotationFound = hasAnnotationInGetter(propertyName, annotationType);
        }
        if (!annotationFound && includeSetter) {
            annotationFound = hasAnnotationInSetter(propertyName, annotationType);
        }
        return annotationFound;
    }

    private boolean hasAnnotationInGetter(String propertyName, Class<? extends Annotation> annotationType) {
        boolean annotationFound = false;
        Method readMethod = beanWrapper.getPropertyDescriptor(propertyName).getReadMethod();
        if (readMethod != null) {
            annotationFound = AnnotationUtils.hasAnnotation(readMethod, annotationType);
        }
        return annotationFound;
    }

    private boolean hasAnnotationInSetter(String propertyName, Class<? extends Annotation> annotationType) {
        boolean annotationFound = false;
        Method writeMethod = beanWrapper.getPropertyDescriptor(propertyName).getWriteMethod();
        if (writeMethod != null) {
            annotationFound = AnnotationUtils.hasAnnotation(writeMethod, annotationType);
        }
        return annotationFound;
    }

}
