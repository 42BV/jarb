package org.jarb.constraint.jsr303;

import static org.jarb.utils.bean.BeanAnnotationScanner.fieldOrGetter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Email;
import org.jarb.constraint.PropertyConstraintDescription;
import org.jarb.constraint.PropertyConstraintEnhancer;
import org.jarb.constraint.PropertyType;

/**
 * Enhances property metadata with types retrieved from the annotations.
 * When, for example, a property is annotated with @Email, we will add
 * an email type to the metadata.
 * 
 * @author Jeroen van Schagen
 * @since 10-06-2011
 */
public class AnnotationPropertyConstraintMetadataTypeEnhancer implements PropertyConstraintEnhancer {
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
    public PropertyConstraintDescription enhance(PropertyConstraintDescription propertyMetadata) {
        for (Map.Entry<Class<? extends Annotation>, PropertyType> mappingEntry : TYPE_MAPPINGS.entrySet()) {
            if (fieldOrGetter().hasAnnotation(propertyMetadata.getPropertyReference(), mappingEntry.getKey())) {
                propertyMetadata.addType(mappingEntry.getValue());
            }
        }
        return propertyMetadata;
    }

}
