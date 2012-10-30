package org.jarbframework.constraint.validation.rules;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.constraint.metadata.database.ColumnMetadata;
import org.jarbframework.constraint.validation.DatabaseConstraintValidationContext;
import org.jarbframework.utils.bean.PropertyReference;

/**
 * Validates a property value on the column metadata.
 * @author Jeroen van Schagen
 * @since 20-10-2012
 */
public class PropertyValueChecker {

    /** Concrete validation logic that should be performed **/
    private final List<PropertyValueValidationRule> validationRules;

    public PropertyValueChecker() {
        validationRules = new ArrayList<PropertyValueValidationRule>();
        validationRules.add(new NotNullValidationRule());
        validationRules.add(new LengthValidationRule());
        validationRules.add(new FractionLengthValidationRule());
    }

    public void validate(Object propertyValue, PropertyReference propertyReference, ColumnMetadata columnMetadata, DatabaseConstraintValidationContext validation) {
        for(PropertyValueValidationRule validationRule : validationRules) {
            validationRule.validate(propertyValue, propertyReference, columnMetadata, validation);
        }
    }
    
}
