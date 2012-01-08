/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

/**
 * Enhances the property with a type whenever a specific
 * type is used inside the property declaration.
 *
 * @author Jeroen van Schagen
 * @date Sep 7, 2011
 */
public class ClassPropertyTypeEnhancer implements PropertyConstraintEnhancer {
    private final Class<?> propertyClass;
    private final String type;

    public ClassPropertyTypeEnhancer(Class<?> propertyClass, String type) {
        this.propertyClass = notNull(propertyClass, "Property class cannot be null");
        this.type = hasText(type, "Type needs to have text");
    }

    @Override
    public PropertyConstraintDescription enhance(PropertyConstraintDescription description) {
        if (propertyClass.isAssignableFrom(description.getJavaType())) {
            description.addType(type);
        }
        return description;
    }

}
