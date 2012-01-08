package org.jarbframework.validation;

import org.jarbframework.constraint.database.ColumnMetadata;
import org.jarbframework.utils.bean.PropertyReference;

/**
 * Database constraint validation step that can be performed during a validate.
 * @author Jeroen van Schagen
 * @since 19-10-2011
 */
public interface DatabaseConstraintValidationStep {

    /**
     * Validates a property value on database column constraints.
     * @param propertyValue the property value to validate
     * @param propertyRef reference to the property
     * @param columnMetadata metadata of the column referenced by our property
     * @param validation the validation result in which violations are stored
     */
    void validate(Object propertyValue, PropertyReference propertyRef, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext validation);

}
