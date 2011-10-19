package org.jarbframework.validation;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.jarbframework.constraint.DatabaseGenerated;
import org.jarbframework.constraint.database.CouldNotBeMappedToColumnException;
import org.jarbframework.constraint.database.DatabaseConstraintRepository;
import org.jarbframework.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.bean.BeanAnnotationScanner;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.bean.PropertyReference;
import org.springframework.beans.BeanUtils;

public class DatabaseConstraintValidator {
    private static final String NOT_NULL_TEMPLATE = "{javax.validation.constraints.NotNull.message}";
    private static final String LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";
    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    private final BeanAnnotationScanner annotationScanner = new BeanAnnotationScanner(true, true);
    private DatabaseConstraintRepository constraintRepository;
    private ViolationMessageBuilder messageBuilder;
    
    public void setConstraintRepository(DatabaseConstraintRepository constraintRepository) {
        this.constraintRepository = constraintRepository;
    }
    
    public void setMessageBuilder(ViolationMessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }
    
    public class DatabaseConstraintValidation {
        private final Set<DatabaseConstraintViolation> violations = new HashSet<DatabaseConstraintViolation>();
        
        public DatabaseConstraintValidation(Object bean) {
            validateBean(bean);
        }
        
        private void validateBean(Object bean) {
            for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(bean.getClass())) {
                validateProperty(bean, property);
            }
        }
        
        private void validateProperty(Object bean, PropertyDescriptor property) {
            PropertyReference propertyRef = new PropertyReference(bean.getClass(), property.getName());
            try {
                ColumnMetadata columnMetadata = constraintRepository.getColumnMetadata(propertyRef);
                if (columnMetadata != null) {
                    final Object propertyValue = ModifiableBean.wrap(bean).getPropertyValue(property.getName());
                    validateValue(bean, propertyRef, propertyValue, columnMetadata);
                }
            } catch (CouldNotBeMappedToColumnException e) {
                // Property has no matching column, and thus will not be validated
            }
        }

        private void validateValue(Object bean, PropertyReference propertyRef, Object propertyValue, ColumnMetadata columnMetadata) {
            if (notNullViolated(propertyRef, propertyValue, columnMetadata)) {
                violations.add(new DatabaseConstraintViolation(propertyRef, NOT_NULL_TEMPLATE));
            }
            if (lengthExceeded(propertyValue, columnMetadata)) {
                String message = messageBuilder.template(LENGTH_TEMPLATE).attribute("max", columnMetadata.getMaximumLength()).value(propertyValue).message();
                violations.add(new DatabaseConstraintViolation(propertyRef, message));
            }
            if (fractionLengthExceeded(propertyValue, columnMetadata)) {
                String message = messageBuilder.template(FRACTION_LENGTH_TEMPLATE).attribute("max", columnMetadata.getFractionLength()).value(propertyValue).message();
                violations.add(new DatabaseConstraintViolation(propertyRef, message));
            }
        }

        private boolean notNullViolated(PropertyReference propertyReference, Object propertyValue, ColumnMetadata columnMetadata) {
            return propertyValue == null && columnMetadata.isRequired() && !isGeneratable(propertyReference, columnMetadata);
        }

        private boolean isGeneratable(PropertyReference propertyReference, ColumnMetadata columnMetadata) {
            return columnMetadata.isGeneratable() || annotationScanner.hasAnnotation(propertyReference, DatabaseGenerated.class);
        }

        private boolean lengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
            boolean lengthExceeded = false;
            if (columnMetadata.hasMaximumLength()) {
                if (propertyValue instanceof String) {
                    lengthExceeded = ((String) propertyValue).length() > columnMetadata.getMaximumLength();
                } else if (propertyValue instanceof Number) {
                    lengthExceeded = numberOfDigits((Number) propertyValue) > columnMetadata.getMaximumLength();
                }
            }
            return lengthExceeded;
        }

        private int numberOfDigits(Number number) {
            return new BigDecimal(number.toString()).precision();
        }

        private boolean fractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
            boolean lengthExceeded = false;
            if (columnMetadata.hasFractionLength() && propertyValue instanceof Number) {
                lengthExceeded = lengthOfFraction((Number) propertyValue) > columnMetadata.getFractionLength();
            }
            return lengthExceeded;
        }

        private int lengthOfFraction(Number number) {
            BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
            return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
        }
    }
    
    /**
     * Checks whether all properties inside a bean satisfy our database constraints.
     */
    public Set<DatabaseConstraintViolation> validate(Object bean) {
        return new DatabaseConstraintValidation(bean).violations;
    }
      
}
