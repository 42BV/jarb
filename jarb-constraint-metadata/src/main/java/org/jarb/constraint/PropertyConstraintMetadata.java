package org.jarb.constraint;

import java.util.Collection;


/**
 * Describes the constraints of a bean property.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of property being described
 */
public interface PropertyConstraintMetadata<T> {
    
    /**
     * Retrieve the property name.
     * @return property name
     */
    String getName();
    
    /**
     * Retrieve the property types.
     * @return property types
     */
    Collection<PropertyType> getTypes();

    /**
     * Retrieve the property java type.
     * @return property java type
     */
    Class<T> getJavaType();

    /**
     * Determine if a property value is required.
     * @return whether property is required
     */
    boolean isRequired();

    /**
     * Retrieve the minimum length of a property value.
     * @return minimum length, if any
     */
    Integer getMinimumLength();

    /**
     * Retrieve the maximum length of a property value.
     * @return maximum length, if any
     */
    Integer getMaximumLength();

    /**
     * Retrieve the (maximum) length of a value fraction.
     * @return fraction length, if any
     */
    Integer getFractionLength();

    /**
     * Retrieve the numeric value radix.
     * @return radix, if any
     */
    Integer getRadix();
    
}
