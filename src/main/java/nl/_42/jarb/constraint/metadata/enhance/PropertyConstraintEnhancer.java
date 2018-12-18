package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;

/**
 * Enhances property constraint description with additional information.
 * 
 * @author Jeroen van Schagen
 * @since 4-6-2011
 */
public interface PropertyConstraintEnhancer {

    /**
     * Enhance a property constraint description object.
     * @param description describes the constraints of a property
     */
    void enhance(PropertyConstraintDescription description);

}
