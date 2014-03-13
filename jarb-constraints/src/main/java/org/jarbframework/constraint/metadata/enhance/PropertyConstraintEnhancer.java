package org.jarbframework.constraint.metadata.enhance;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Enhances property constraint description with additional information.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface PropertyConstraintEnhancer {

    /**
     * Enhance a property constraint description object.
     * @param <T> type of property being described
     * @param description describes the constraints of a property
     */
    void enhance(PropertyConstraintDescription description);

}
