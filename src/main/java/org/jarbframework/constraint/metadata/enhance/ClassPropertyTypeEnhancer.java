package org.jarbframework.constraint.metadata.enhance;

import static org.jarbframework.utils.Asserts.hasText;
import static org.jarbframework.utils.Asserts.notNull;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

/**
 * Enhances the property with a type whenever a specific
 * type is used inside the property declaration.
 *
 * @author Jeroen van Schagen
 * @since Sep 7, 2011
 */
public class ClassPropertyTypeEnhancer implements PropertyConstraintEnhancer {
    
    private final Class<?> propertyClass;
    
    private final String typeName;

    public ClassPropertyTypeEnhancer(Class<?> propertyClass, String typeName) {
        this.propertyClass = notNull(propertyClass, "Property class cannot be null");
        this.typeName = hasText(typeName, "Type name should have text");
    }

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (propertyClass.isAssignableFrom(description.getJavaType())) {
            description.addType(typeName);
        }
    }

}
