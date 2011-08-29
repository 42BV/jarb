/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.bean;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.util.ReflectionUtils.findField;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

/**
 * Default implementation of {@link BeanAnnotationScanner}.
 *
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class BeanAnnotationScannerImpl implements BeanAnnotationScanner {
    private final boolean includeGetter;
    private final boolean includeSetter;

    public BeanAnnotationScannerImpl(boolean includeGetter, boolean includeSetter) {
        this.includeGetter = includeGetter;
        this.includeSetter = includeSetter;
    }

    @Override
    public <T extends Annotation> T findAnnotation(Class<?> beanClass, Class<T> annotationType) {
        return AnnotationUtils.findAnnotation(beanClass, annotationType);
    }

    @Override
    public boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        return findAnnotation(beanClass, annotationType) != null;
    }

    @Override
    public <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        T annotation = null;
        // Extract annotation from field declaration
        Field field = findField(propertyReference.getBeanClass(), propertyReference.getName());
        if (field != null) {
            annotation = field.getAnnotation(annotationType);
        }
        // Otherwise attempt to extract from access methods
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyReference.getBeanClass(), propertyReference.getName());
        if (propertyDescriptor != null) {
            if (annotation == null && includeGetter && propertyDescriptor.getReadMethod() != null) {
                annotation = AnnotationUtils.findAnnotation(propertyDescriptor.getReadMethod(), annotationType);
            }
            if (annotation == null && includeSetter && propertyDescriptor.getWriteMethod() != null) {
                annotation = AnnotationUtils.findAnnotation(propertyDescriptor.getWriteMethod(), annotationType);
            }
        }
        return annotation;
    }

    @Override
    public boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType) {
        return findAnnotation(propertyReference, annotationType) != null;
    }

}
