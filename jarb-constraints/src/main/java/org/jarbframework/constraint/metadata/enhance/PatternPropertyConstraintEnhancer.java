/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import java.util.Collection;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.Annotations;

/**
 * Stores the @Pattern in our description.
 *
 * @author Jeroen van Schagen
 * @since Mar 3, 2014
 */
public class PatternPropertyConstraintEnhancer implements PropertyConstraintEnhancer {
    
    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<javax.validation.constraints.Pattern> annotations = 
                Annotations.getAnnotations(description.toReference(), javax.validation.constraints.Pattern.class);
        if (!annotations.isEmpty()) {
            description.setPattern(annotations.iterator().next().regexp());
        }
    }
    
}
