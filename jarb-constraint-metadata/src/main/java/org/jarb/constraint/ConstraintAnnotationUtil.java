package org.jarb.constraint;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

/**
 * Provides constraint annotation based utilities.
 * 
 * @author Jeroen van Schagen
 * @since 1-06-2011
 */
public final class ConstraintAnnotationUtil {

    /**
     * Retrieve all bean property constraint annotations. During retrieval
     * we scan the property field and getter method for annotations.
     * @param <T> type of annotation
     * @param beanClass class of the bean containing our property
     * @param propertyDescriptor description of the property
     * @param annotationClass type of annotation we are fetching
     * @return annotations of the specified type, on the bean property
     */
    public static <T extends Annotation> List<T> getPropertyAnnotations(Class<?> beanClass, PropertyDescriptor propertyDescriptor, Class<T> annotationClass) {
        List<T> annotationList = new ArrayList<T>();
        if (propertyDescriptor.getReadMethod() != null) { // Hidden properties have no getter method
            T getterAnnotation = propertyDescriptor.getReadMethod().getAnnotation(annotationClass);
            if (getterAnnotation != null) {
                annotationList.add(getterAnnotation);
            }
        }
        Field propertyField = ReflectionUtils.findField(beanClass, propertyDescriptor.getName());
        if (propertyField != null) { // Some properties, such as "class", have no corresponding field
            T fieldAnnotation = propertyField.getAnnotation(annotationClass);
            if (fieldAnnotation != null) {
                annotationList.add(fieldAnnotation);
            }
        }
        return annotationList;
    }

    private ConstraintAnnotationUtil() {
        // Prevent initialization
    }

}
