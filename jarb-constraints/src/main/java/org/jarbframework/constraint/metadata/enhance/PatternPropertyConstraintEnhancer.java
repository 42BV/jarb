/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.bean.AnnotationScanner.fieldOrGetter;

import java.util.Collection;

import javax.validation.constraints.Pattern;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Stores the annotated pattern in our description.
 *
 * @author Jeroen van Schagen
 * @since Mar 3, 2014
 */
public class PatternPropertyConstraintEnhancer implements PropertyConstraintEnhancer {
    
    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        Collection<Pattern> annotations = fieldOrGetter().getAnnotations(description.toReference(), Pattern.class);
        if (!annotations.isEmpty()) {
            description.setPattern(annotations.iterator().next().regexp());
        }
        return description;
    }
    
}
