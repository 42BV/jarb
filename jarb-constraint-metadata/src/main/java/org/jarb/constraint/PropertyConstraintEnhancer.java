package org.jarb.constraint;

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
     * @param propertyConstraints describes the constraints of a property
     * @return (enhanced) property constraint description
     */
    PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints);

}
