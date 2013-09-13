package org.jarbframework.utils.bean;

import static org.jarbframework.utils.Asserts.state;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Searches for annotations in class and property declarations. 
 * 
 * @author Jeroen van Schagen
 * @date Aug 29, 2011
 */
public class BeanAnnotationScanner {
    
    private final boolean includeGetter;

    /**
     * Construct a new {@link BeanAnnotationScanner}.
     * @param includeGetter determines if getter methods should be scanned
     */
    public BeanAnnotationScanner(boolean includeGetter) {
        this.includeGetter = includeGetter;
    }

    public static BeanAnnotationScanner field() {
        return new BeanAnnotationScanner(false);
    }

    public static BeanAnnotationScanner fieldOrGetter() {
        return new BeanAnnotationScanner(true);
    }

    /**
     * Find a specific annotation on the class declaration.
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @return desired annotation, as declared on the class, if any
     */
    public <T extends Annotation> T findAnnotation(Class<?> beanClass, Class<T> annotationType) {
        return AnnotationUtils.findAnnotation(beanClass, annotationType);
    }

    /**
     * Check if a specific annotation is declared on the class.
     * @param beanClass bean that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public boolean hasAnnotation(Class<?> beanClass, Class<? extends Annotation> annotationType) {
        return findAnnotation(beanClass, annotationType) != null;
    }

    /**
     * Find a specific annotation on the property declaration. Note that we only
     * expect one matching annotation to be found, otherwise an exception is thrown.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @return desired annotation, as declared on the property, if any
     */
    public <T extends Annotation> T findAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
        Collection<T> annotations = getAnnotations(propertyReference, annotationType);
        if (annotations.isEmpty()) {
            return null;
        } else {
            state(annotations.size() == 1, "Found more than one matching annotation.");
            return annotations.iterator().next();
        }
    }

    /**
     * Find a specific annotation on the property declaration.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotation that should be looked for
     * @return desired annotation, as declared on the property, if any
     */
    public <T extends Annotation> Collection<T> getAnnotations(PropertyReference propertyReference, Class<T> annotationType) {
        propertyReference = BeanProperties.getFinalProperty(propertyReference);
        
        Collection<T> annotations = new ArrayList<T>();
                
        T fieldAnnotation = getFieldAnnotation(propertyReference, annotationType);
        if (fieldAnnotation != null) {
        	annotations.add(fieldAnnotation);
        }
        
        T getterAnnotation = getGetterAnnotation(propertyReference, annotationType);
        if (getterAnnotation != null) {
        	annotations.add(getterAnnotation);
        }
        
        return annotations;
    }

	private <T extends Annotation> T getGetterAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
		T annotation = null;
		
		PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(propertyReference.getBeanClass(), propertyReference.getName());
        if (propertyDescriptor != null) {
            if (includeGetter && propertyDescriptor.getReadMethod() != null) {
            	annotation = AnnotationUtils.findAnnotation(propertyDescriptor.getReadMethod(), annotationType);
            }
        }
        
        return annotation;
	}

	private <T extends Annotation> T getFieldAnnotation(PropertyReference propertyReference, Class<T> annotationType) {
		T annotation = null;
		
		Field field = ReflectionUtils.findField(propertyReference.getBeanClass(), propertyReference.getName());
        if (field != null) {
        	annotation = field.getAnnotation(annotationType);
        }
        
        return annotation;
	}

    /**
     * Check if a specific property is declared on the property.
     * @param propertyReference property that should contain the annotation
     * @param annotationType type of annotations that should be looked for
     * @return {@code true} if an annotation could be found, else {@code false}
     */
    public boolean hasAnnotation(PropertyReference propertyReference, Class<? extends Annotation> annotationType) {
        return findAnnotation(propertyReference, annotationType) != null;
    }

}
