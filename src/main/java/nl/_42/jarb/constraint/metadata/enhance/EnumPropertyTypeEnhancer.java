package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;

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
