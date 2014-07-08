/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.Asserts.notNull;

import java.lang.annotation.Annotation;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.constraint.metadata.types.PropertyType;
import org.jarbframework.utils.bean.Annotations;

/**
 * Enhances our property type description from a @PropertyType annotation.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class PropertyTypeEnhancer implements PropertyConstraintEnhancer {
    
    private final Class<? extends Annotation> annotationClass;
        
    public PropertyTypeEnhancer() {
        this(PropertyType.class);
    }

    public PropertyTypeEnhancer(Class<? extends Annotation> annotationClass) {
        this.annotationClass = notNull(annotationClass, "Annotation class cannot be null");
    }
    
    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (Annotations.hasAnnotation(description.toReference(), annotationClass)) {
            Annotation annotation = Annotations.getAnnotations(description.toReference(), annotationClass).iterator().next();
            if (annotation instanceof PropertyType) {
                description.addType(((PropertyType) annotation).value());
            } else {
                PropertyType customType = annotation.annotationType().getAnnotation(PropertyType.class);
                if (customType != null) {
                    description.addType(customType.value());
                }
            }
        }
    }

}
