package nl._42.jarb.constraint.validation;

import nl._42.jarb.constraint.metadata.database.ColumnMetadata;
import nl._42.jarb.utils.bean.PropertyReference;

/**
 * Database constraint validation step that can be performed during a validate.
 * @author Jeroen van Schagen
 * @since 19-10-2011
 */
public interface DatabaseConstraintValidationStep {

    /**
     * Validates a property value on database column constraints.
     * @param value the property value to validate
     * @param valueType the property type
     * @param reference reference to the property
     * @param columnMetadata metadata of the column referenced by our property
     * @param validation the validation result in which violations are stored
     */
    void validate(Object value, Class<?> valueType, PropertyReference reference, ColumnMetadata columnMetadata, DatabaseValidationContext validation);

}
