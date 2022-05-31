package nl._42.jarb.constraint.metadata.enhance;

import nl._42.jarb.constraint.metadata.PropertyConstraintDescription;
import nl._42.jarb.utils.bean.Annotations;

import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Whenever a property is annotated as @NotNull, it is required.
 * 
 * @author Jeroen van Schagen
 * @since 31-05-2011
 */
public class SizePropertyConstraintEnhancer implements PropertyConstraintEnhancer {

    @Override
    public void enhance(PropertyConstraintDescription description) {
        Collection<Size> annotations = Annotations.getAnnotations(description.toReference(), Size.class);
        annotations.stream().mapToInt(annotation -> annotation.min()).min().ifPresent(min -> {
            if (min == 0) {
                description.setRequired(false);
            }
        });
    }

}
