package org.jarb.constraint;

import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

/**
 * Whenever a property is annotated as @AutoIncremental a value
 * will not be required, as it can be generated on demand.
 * 
 * @author Jeroen van Schagen
 * @since 6 September 2011
 */
public class AutoIncrementalPropertyEnhancer implements PropertyConstraintEnhancer {

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        if (fieldOrGetter().hasAnnotation(propertyConstraints.toPropertyReference(), AutoIncremental.class)) {
            propertyConstraints.setRequired(false);
        }
        return propertyConstraints;
    }

}
