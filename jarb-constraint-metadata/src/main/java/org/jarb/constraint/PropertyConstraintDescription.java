package org.jarb.constraint;

/**
 * Describes the constraints of a bean property.
 * 
 * @author Jeroen van Schagen
 * @since 8-6-2011
 *
 * @param <T> type of property being described
 */
public interface PropertyConstraintDescription<T> {
    
    /**
     * Retrieve the property name.
     * @return property name
     */
    String getPropertyName();

    /**
     * Retrieve the property type.
     * @return property type
     */
    Class<T> getPropertyType();

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
