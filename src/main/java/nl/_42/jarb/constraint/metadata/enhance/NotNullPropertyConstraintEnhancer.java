package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

/**
 * Whenever a property is annotated as @NotNull, it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class NotNullPropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public void enhance(PropertyConstraintDescription description) {
        if (Annotations.hasAnnotation(description.toReference(), jakarta.validation.constraints.NotNull.class)) {
            description.setRequired(true);
        }
    }

}
