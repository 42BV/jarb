package org.jarbframework.constraint.validation.rules;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseValidationContext;
import org.jarbframework.utils.bean.PropertyReference;

/**
 * Database constraint validation step that can be performed during a validate.
 * @author Jeroen van Schagen
 * @since 19-10-2011
 */
public interface PropertyValueValidationRule {

    /**
     * Validates a property value on database column constraints.
     * @param propertyValue the property value to validate
     * @param propertyReference reference to the property
     * @param columnMetadata metadata of the column referenced by our property
     * @param validation the validation result in which violations are stored
     */
    void validate(Object propertyValue, PropertyReference propertyReference, ColumnMetadata columnMetadata, DatabaseValidationContext validation);

}
