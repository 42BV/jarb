/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import java.lang.annotation.Annotation;
import java.util.Collection;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.constraint.metadata.types.PropertyType;
import nl._42.jarb.utils.bean.Annotations;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Enhances our property type description from a @PropertyType annotation.
 *
 * @author Jeroen van Schagen
 * @since Mar 4, 2014
 */
public class PropertyTypeEnhancer implements PropertyConstraintEnhancer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<Annotation> annotations = Annotations.getAnnotations(description.toReference());
        for (Annotation annotation : annotations) {
            if (annotation instanceof PropertyType) {
                description.addType(((PropertyType) annotation).value());
            } else {
                PropertyType customType = AnnotationUtils.findAnnotation(annotation.annotationType(), PropertyType.class);
                if (customType != null) {
                    description.addType(customType.value());
                }
            }
        }
    }

}
