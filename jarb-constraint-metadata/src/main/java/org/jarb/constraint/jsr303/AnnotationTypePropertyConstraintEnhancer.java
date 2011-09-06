package org.jarb.constraint.jsr303;

import static org.jarb.utils.Conditions.hasText;
import static org.jarb.utils.Conditions.notNull;
import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.lang.annotation.Annotation;

import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;

public class AnnotationTypePropertyConstraintEnhancer implements PropertyConstraintEnhancer {
    private final Class<? extends Annotation> annotationType;
    private final String type;
    
    public AnnotationTypePropertyConstraintEnhancer(Class<? extends Annotation> annotationType, String type) {
        this.annotationType = notNull(annotationType, "Annotation type cannot be null");
        this.type = hasText(type, "Type needs to have text");
    }
    
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        if (fieldOrGetter().hasAnnotation(propertyConstraints.getPropertyReference(), annotationType)) {
            propertyConstraints.addType(type);
        }
        return propertyConstraints;
    }
    
}
