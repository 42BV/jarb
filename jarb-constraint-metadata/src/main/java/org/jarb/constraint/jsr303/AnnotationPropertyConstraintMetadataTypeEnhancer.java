package org.jarb.constraint.jsr303;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarb.constraint.PropertyConstraintMetadata;
import org.jarb.constraint.PropertyConstraintMetadataEnhancer;
import org.jarb.constraint.PropertyType;

/**
 * Enhances property metadata with types retrieved from the annotations.
 * When, for example, a property is annotated with @Email, we will add
 * an email type to the metadata.
 * 
 * @author Jeroen van Schagen
 * @since 10-06-2011
 */
public class AnnotationPropertyConstraintMetadataTypeEnhancer implements PropertyConstraintMetadataEnhancer {
    private static final Map<Class<? extends Annotation>, PropertyType> TYPE_MAPPINGS;

    static {
        TYPE_MAPPINGS = new HashMap<Class<? extends Annotation>, PropertyType>();
        TYPE_MAPPINGS.put(CreditCardNumber.class, PropertyType.CREDID_CARD);
        TYPE_MAPPINGS.put(Email.class, PropertyType.EMAIL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> PropertyConstraintMetadata<T> enhance(PropertyConstraintMetadata<T> propertyMetadata) {
        for (Map.Entry<Class<? extends Annotation>, PropertyType> mappingEntry : TYPE_MAPPINGS.entrySet()) {
            if (ConstraintAnnotationScanner.isPropertyAnnotated(propertyMetadata.getPropertyReference(), mappingEntry.getKey())) {
                propertyMetadata.addType(mappingEntry.getValue());
            }
        }
        return propertyMetadata;
    }

}
