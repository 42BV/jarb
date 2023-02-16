/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import java.util.Collection;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

/**
 * Stores the @Pattern in our description.
 *
 * @author Jeroen van Schagen
 * @since Mar 3, 2014
 */
public class PatternPropertyConstraintEnhancer implements PropertyConstraintEnhancer {
    
    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<jakarta.validation.constraints.Pattern> annotations =
                Annotations.getAnnotations(description.toReference(), jakarta.validation.constraints.Pattern.class);
        if (!annotations.isEmpty()) {
            description.setPattern(annotations.iterator().next().regexp());
        }
    }
    
}
