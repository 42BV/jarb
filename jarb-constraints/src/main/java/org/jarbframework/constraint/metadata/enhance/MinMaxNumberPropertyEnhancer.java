/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.enhance;

import java.util.Collection;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;
import org.jarbframework.utils.bean.AnnotationScanner;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Mar 13, 2014
 */
public class MinMaxNumberPropertyEnhancer implements PropertyConstraintEnhancer {
    
    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<javax.validation.constraints.Min> mins = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Min.class);
        Collection<javax.validation.constraints.Max> maxs = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Max.class);

        if (Number.class.isAssignableFrom(description.getJavaType())) {
            Number minValue = (Number) description.getMin();
            Number maxValue = (Number) description.getMax();
            
            for (javax.validation.constraints.Min min : mins) {
                if (minValue == null || min.value() < minValue.longValue()) {
                    minValue = min.value();
                }
            }
            
            for (javax.validation.constraints.Max max : maxs) {
                if (maxValue == null || max.value() > maxValue.longValue()) {
                    maxValue = max.value();
                }
            }

            description.setMin(minValue);
            description.setMax(maxValue);
        }
    }
    
}
