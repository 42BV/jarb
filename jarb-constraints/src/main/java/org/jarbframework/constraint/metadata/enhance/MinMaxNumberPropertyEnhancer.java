/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import java.util.Collection;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.AnnotationScanner;

/**
 * Enhances the property description with @Min and @Max values.
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
public class MinMaxNumberPropertyEnhancer implements PropertyConstraintEnhancer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<javax.validation.constraints.Min> mins = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Min.class);
        Collection<javax.validation.constraints.Max> maxs = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Max.class);

        if (Number.class.isAssignableFrom(description.getJavaType())) {
            Number minValue = getMinimalValue(description, mins);
            Number maxValue = getMaximumValue(description, maxs);

            description.setMin(minValue);
            description.setMax(maxValue);
        }
    }

    /**
     * Retrieve the minimal value.
     * 
     * @param description the current description
     * @param mins the annotations
     * @return the minimal value
     */
    private Number getMinimalValue(PropertyConstraintDescription description, Collection<javax.validation.constraints.Min> mins) {
        Number value = (Number) description.getMin();
        for (javax.validation.constraints.Min min : mins) {
            if (value == null || min.value() < value.longValue()) {
                value = min.value();
            }
        }
        return value;
    }
    
    /**
     * Retrieve the maximum value.
     * 
     * @param description the current description
     * @param maxs the annotations
     * @return the maximum value
     */
    private Number getMaximumValue(PropertyConstraintDescription description, Collection<javax.validation.constraints.Max> maxs) {
        Number value = (Number) description.getMax();
        for (javax.validation.constraints.Max max : maxs) {
            if (value == null || max.value() > value.longValue()) {
                value = max.value();
            }
        }
        return value;
    }

}
