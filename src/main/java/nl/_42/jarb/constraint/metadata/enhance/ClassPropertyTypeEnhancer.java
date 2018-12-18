package nl._42.jarb.constraint.metadata.enhance;

import static nl._42.jarb.utils.Asserts.hasText;
import static nl._42.jarb.utils.Asserts.notNull;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;

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
