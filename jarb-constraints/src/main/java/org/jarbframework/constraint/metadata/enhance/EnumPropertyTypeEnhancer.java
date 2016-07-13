package org.jarbframework.constraint.metadata.enhance;

import org.jarbframework.constraint.metadata.PropertyConstraintDescription;

public class EnumPropertyTypeEnhancer implements PropertyConstraintEnhancer {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (description.getJavaType().isEnum()) {
            description.addType("enum");
        }
    }
    
}
