package nl._42.jarb.constraint.metadata.enhance;

import static nl._42.jarb.utils.Asserts.hasText;
import static nl._42.jarb.utils.Asserts.notNull;

import java.lang.annotation.Annotation;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

/**
 * Enhances the property with a type whenever a specific
 * annotation is present inside the property declaration.
 *
 * @author Jeroen van Schagen
 * @since Sep 7, 2011
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
