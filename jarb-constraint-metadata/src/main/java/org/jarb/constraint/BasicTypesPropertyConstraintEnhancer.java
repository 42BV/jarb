package org.jarb.constraint;

import java.util.Date;

/**
 * Enhances the property constraint description with a string
 * based type for various commonly used java types.
 * @author Jeroen van Schagen
 * @since 6 September 2011
 */
public class BasicTypesPropertyConstraintEnhancer implements PropertyConstraintEnhancer {
    public static final String TEXT_TYPE = "text";
    public static final String DATE_TYPE = "date";
    public static final String NUMBER_TYPE = "number";

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyConstraints) {
        if(Date.class.isAssignableFrom(propertyConstraints.getJavaType())) {
            propertyConstraints.addType(DATE_TYPE);
        } else if(Number.class.isAssignableFrom(propertyConstraints.getJavaType())) {
            propertyConstraints.addType(NUMBER_TYPE);
        } else if(propertyConstraints.getJavaType().equals(String.class)) {
            propertyConstraints.addType(TEXT_TYPE);
        }
        return propertyConstraints;
    }

}
