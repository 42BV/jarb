package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.state;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Searches for annotations in class and property declarations. 
 * 
 * @author Jeroen van Schagen
 * @since Aug 29, 2011
 */
public class Annotations {

    private Annotations() {
    }

    /**
     * Check if a specific annotation is declared on the class.
     * 
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public static boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        return AnnotationUtils.findAnnotation(beanClass, annotationType) != null;
    }

    /**
     * Find a specific annotation on the property declaration.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @param <T> annotation type of instance
     * @return desired annotation, as declared on the property, if any
     */
    public static <T extends Annotation> Collection<T> getAnnotations(PropertyReference propertyReference, Class<T> annotationType) {
        PropertyReference finalReference = Beans.getFinalProperty(propertyReference);

        Set<T> annotations = new HashSet<T>();
        addIfNotNull(annotations, getFieldAnnotation(finalReference, annotationType));
        addIfNotNull(annotations, getGetterAnnotation(finalReference, annotationType));
        return annotations;
    }

    private static <T extends Annotation> T getGetterAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(propertyReference.getBeanClass(), propertyReference.getPropertyName());

        T annotation = null;
        if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null) {
            annotation = AnnotationUtils.findAnnotation(propertyDescriptor.getReadMethod(), annotationType);
        }
        return annotation;
	}

    private static <T extends Annotation> T getFieldAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        Field field = ReflectionUtils.findField(propertyReference.getBeanClass(), propertyReference.getPropertyName());
        
        T annotation = null;
        if (field != null) {
        	annotation = field.getAnnotation(annotationType);
        }
        return annotation;
	}
    
    private static <T> void addIfNotNull(Collection<T> collection, T element) {
        if (element != null) {
            collection.add(element);
        }
    }

    /**
     * Check if a specific property is declared on the property.
     * 
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public static boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType) {
        return findAnnotation(propertyReference, annotationType) != null;
    }

    private static <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        Collection<T> annotations = getAnnotations(propertyReference, annotationType);
        if (annotations.isEmpty()) {
            return null;
        } else {
            state(annotations.size() == 1, "Found more than one matching annotation.");
            return annotations.iterator().next();
        }
    }

}
