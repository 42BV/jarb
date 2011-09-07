package org.jarb.validation;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidatorFactory;

import org.jarb.constraint.database.CouldNotBeMappedToColumnException;
import org.jarb.constraint.database.DatabaseConstraintRepository;
import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarbframework.utils.BeanAccessor;
import org.jarbframework.utils.bean.BeanAnnotationScanner;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.bean.PropertyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Validates the property values of a bean satisfy our database constraints.
 * Performed database checks include:
 * 
 * <ul>
 *  <li>Not nullable columns cannot have {@code null} values, unless they are generated</li>
 *  <li>Column length can never be exceeded. (char and number based)</li>
 *  <li>Fraction length in number column cannot be exceeded</li>
 * </ul>
 * 
 * Whenever a database constraint is violated, a constraint violation will be
 * added for that property. Multiple constraint violations can occur, even on
 * the same property.
 * 
 * <p>
 * 
 * In order for this validator to work, it has to be constructed by a Spring
 * context aware factory. Also a {@link DatabaseConstraintRepository} bean
 * has to be present in the application context.
 * 
 * @author Jeroen van Schagen
 * @since 23-05-2011
 */
public class DatabaseConstrainedValidator implements ConstraintValidator<DatabaseConstrained, Object>, ApplicationContextAware {
    public static final String DEFAULT_VALIDATOR_FACTORY_ID = "validator";

    private final Logger logger = LoggerFactory.getLogger(DatabaseConstrainedValidator.class);

    // Violation message templates
    private static final String NOT_NULL_TEMPLATE = "{javax.validation.constraints.NotNull.message}";
    private static final String LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.Length.message}";
    private static final String FRACTION_LENGTH_TEMPLATE = "{org.jarb.validation.DatabaseConstraint.FractionLength.message}";

    private final BeanAnnotationScanner annotationScanner = new BeanAnnotationScanner(true, true);

    /** Used to retrieve column meta-data repository during initialization **/
    private BeanAccessor beanAccessor;

    /** Provides the meta-data of columns in our database **/
    private DatabaseConstraintRepository databaseConstraintRepository;
    /** Allows custom violation messages to be build **/
    private ViolationMessageBuilder messageBuilder;

    private DatabaseConstrained annotation;

    /**
     * {@inheritDoc}
     * <p>
     * Checks whether all properties inside a bean satisfy our database constraints.
     */
    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext context) {
        boolean beanIsValid = true;
        context.disableDefaultConstraintViolation(); // Only provide property violations
        for (PropertyDescriptor property : BeanUtils.getPropertyDescriptors(bean.getClass())) {
            if (!isValidProperty(bean, property, context)) {
                beanIsValid = false; // Continue validation, retrieving all violations
            }
        }
        return beanIsValid;
    }

    /**
     * Check if a property satisfies our database column constraints.
     * @param bean the bean being verified
     * @param property description of the property (column) being checked now
     * @param context validator context, used to create custom error messages
     * @return {@code true} if the property satisfies our database constraints, else {@code false}
     */
    private boolean isValidProperty(Object bean, PropertyDescriptor property, ConstraintValidatorContext context) {
        boolean propertyIsValid = true;
        try {
            ColumnMetadata columnMetadata = databaseConstraintRepository.getColumnMetadata(bean.getClass(), property.getName());
            if (columnMetadata != null) {
                final Object propertyValue = ModifiableBean.wrap(bean).getPropertyValue(property.getName());
                propertyIsValid = isValidValue(bean, property.getName(), propertyValue, columnMetadata, context);
            } else {
                logger.warn("No column meta data has been defined for '{}' ({})", new Object[] { property.getName(), bean.getClass().getSimpleName() });
            }
        } catch (CouldNotBeMappedToColumnException e) {
            // Property has no matching column, and thus will not be validated
        }
        return propertyIsValid;
    }

    /**
     * Determine if a property value satisfies all database constraints.
     * @param value the (property) value being checked on database constraints
     * @param propertyName name of the property being verified, used to couple violations on that property
     * @param columnMetadata provides the database constraints for that column
     * @param context validator context, used to create custom violation messages
     * @return {@code true} if the value satisfies our database constraints, else {@code false}
     */
    private boolean isValidValue(Object bean, String propertyName, Object propertyValue, ColumnMetadata columnMetadata, ConstraintValidatorContext context) {
        boolean valueIsValid = true;
        if (notNullViolated(new PropertyReference(bean.getClass(), propertyName), propertyValue, columnMetadata)) {
            context.buildConstraintViolationWithTemplate(NOT_NULL_TEMPLATE).addNode(propertyName).addConstraintViolation();
            valueIsValid = false;
        }
        if (lengthExceeded(propertyValue, columnMetadata)) {
            String message = messageBuilder.template(LENGTH_TEMPLATE).attribute("max", columnMetadata.getMaximumLength()).value(propertyValue).message();
            context.buildConstraintViolationWithTemplate(message).addNode(propertyName).addConstraintViolation();
            valueIsValid = false;
        }
        if (fractionLengthExceeded(propertyValue, columnMetadata)) {
            String message = messageBuilder.template(FRACTION_LENGTH_TEMPLATE).attribute("max", columnMetadata.getFractionLength()).value(propertyValue)
                    .message();
            context.buildConstraintViolationWithTemplate(message).addNode(propertyName).addConstraintViolation();
            valueIsValid = false;
        }
        return valueIsValid;
    }

    /**
     * Check if a database not null constraint has been violated. The not null check is
     * violated whenever a required value, that cannot be generated, is {@code null}.
     * @param propertyValue the property value to check
     * @param columnMetadata provides column information
     * @return {@code true} if the not null constraint was violated, else {@code false}
     */
    protected boolean notNullViolated(PropertyReference propertyReference, Object propertyValue, ColumnMetadata columnMetadata) {
        return propertyValue == null && columnMetadata.isRequired() && !isGeneratable(propertyReference, columnMetadata);
    }

    private boolean isGeneratable(PropertyReference propertyReference, ColumnMetadata columnMetadata) {
        return columnMetadata.isGeneratable() || annotationScanner.hasAnnotation(propertyReference, annotation.autoIncrementalClass());
    }

    /**
     * Check if a database length constraint has been violated.
     * @param propertyValue the property value to check
     * @param columnMetadata provides column information
     * @return {@code true} if the length constraint was violated, else {@code false}
     */
    protected boolean lengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
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

    /**
     * Determine the total number of digits in a number.
     * @param number the number we are counting digits on
     * @return number of digits in the provided value
     */
    private int numberOfDigits(Number number) {
        return new BigDecimal(number.toString()).precision();
    }

    /**
     * Check if a database fraction length constraint has been violated.
     * @param propertyValue the property value to check
     * @param columnMetadata provides column information
     * @return {@code true} if the fraction length constraint was violated, else {@code false}
     */
    protected boolean fractionLengthExceeded(Object propertyValue, ColumnMetadata columnMetadata) {
        boolean lengthExceeded = false;
        if (columnMetadata.hasFractionLength() && propertyValue instanceof Number) {
            lengthExceeded = lengthOfFraction((Number) propertyValue) > columnMetadata.getFractionLength();
        }
        return lengthExceeded;
    }

    /**
     * Determine the length of our fraction (right of the decimal point).
     * @param number the number we are counting fraction length on
     * @return length of our number's fraction, if any
     */
    private int lengthOfFraction(Number number) {
        BigDecimal numberAsBigDecimal = new BigDecimal(number.toString());
        return numberAsBigDecimal.scale() < 0 ? 0 : numberAsBigDecimal.scale();
    }

    // Initialization

    /**
     * {@inheritDoc}
     * <p>
     * Asserts that every required dependency has been injected correctly.
     */
    @Override
    public void initialize(DatabaseConstrained annotation) {
        Assert.notNull(beanAccessor, "Bean accessor cannot be null.");
        if (databaseConstraintRepository == null) {
            databaseConstraintRepository = beanAccessor.findBean(DatabaseConstraintRepository.class, annotation.repository());
            ValidatorFactory validatorFactory = beanAccessor.findBean(ValidatorFactory.class, annotation.factory(), DEFAULT_VALIDATOR_FACTORY_ID);
            messageBuilder = new ViolationMessageBuilder(validatorFactory.getMessageInterpolator());
        }
        this.annotation = annotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanAccessor = new BeanAccessor(applicationContext);
    }

}
