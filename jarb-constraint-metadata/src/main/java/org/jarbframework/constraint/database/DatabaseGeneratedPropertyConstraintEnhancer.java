package org.jarbframework.constraint.database;

import static org.jarbframework.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import org.jarbframework.constraint.PropertyConstraintDescription;
import org.jarbframework.constraint.PropertyConstraintEnhancer;

/**
 * Whenever a property is annotated as @AutoIncremental a value
 * will not be required, as it can be generated on demand.
 * 
 * @author Jeroen van Schagen
 * @since 6 September 2011
 */
public class DatabaseGeneratedPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        if (fieldOrGetter().hasAnnotation(propertyConstraints.toPropertyReference(), DatabaseGenerated.class)) {
            propertyConstraints.setRequired(false);
        }
        return propertyConstraints;
    }

}
