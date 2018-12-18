/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import org.springframework.util.Assert;

/**
 * Enhances the constraints with the primitive min and max values.
 *
 * @author Jeroen van Schagen
 * @since Jun 27, 2014
 */
public class MinMaxNumberPropertyEnhancer implements PropertyConstraintEnhancer {
    
    private final Class<?> propertyType;
    
    private final long minValue;
    
    private final long maxValue;
    
    public MinMaxNumberPropertyEnhancer(Class<?> propertyType, long minValue, long maxValue) {
        Assert.state(Number.class.isAssignableFrom(propertyType), "Property type must be a number.");

        this.propertyType = propertyType;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (propertyType.equals(description.getJavaType())) {
            description.addMin(minValue);
            description.addMax(maxValue);
        }
    }
    
}
