package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import java.lang.annotation.Annotation;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.Annotations;

/**
 * Enhances the property with a type whenever a specific
 * annotation is present inside the property declaration.
 *
 * @author Jeroen van Schagen
 * @date Sep 7, 2011
 */
public class AnnotationPropertyTypeEnhancer implements PropertyConstraintEnhancer {
    
    private final Class<? extends Annotation> annotationClass;
    
    private final String typeName;

    public AnnotationPropertyTypeEnhancer(Class<? extends Annotation> annotationClass, String typeName) {
        this.annotationClass = notNull(annotationClass, "Annotation class cannot be null");
        this.typeName = hasText(typeName, "Type name needs to have text");
    }

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (Annotations.hasAnnotation(description.toReference(), annotationClass)) {
            description.addType(typeName);
        }
    }

}
