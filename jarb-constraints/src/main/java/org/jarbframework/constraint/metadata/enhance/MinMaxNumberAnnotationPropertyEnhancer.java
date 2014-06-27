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
public class MinMaxNumberAnnotationPropertyEnhancer implements PropertyConstraintEnhancer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        final Class<?> javaType = description.getJavaType();
        if (Number.class.isAssignableFrom(javaType)) {
            Collection<javax.validation.constraints.Min> mins = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Min.class);
            Collection<javax.validation.constraints.Max> maxs = AnnotationScanner.getAnnotations(description.toReference(), javax.validation.constraints.Max.class);
            
            for (javax.validation.constraints.Min min : mins) {
                description.addMin(min.value());
            }
            for (javax.validation.constraints.Max max : maxs) {
                description.addMax(max.value());
            }
        }
    }

}
