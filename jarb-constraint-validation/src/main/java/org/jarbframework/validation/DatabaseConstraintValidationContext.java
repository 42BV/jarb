package org.jarbframework.validation;

import javax.validation.ConstraintValidatorContext;

import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.validation.ViolationMessageBuilder.ViolationMessageTemplate;

public final class DatabaseConstraintValidationContext {
    private final ConstraintValidatorContext validatorContext;
    private final ViolationMessageBuilder messageBuilder;
    private boolean valid = true;
    
    DatabaseConstraintValidationContext(ConstraintValidatorContext validatorContext, ViolationMessageBuilder messageBuilder) {
        this.validatorContext = validatorContext;
        this.messageBuilder = messageBuilder;
        validatorContext.disableDefaultConstraintViolation();
    }
    
    /**
     * Determine if no violations were detected in this validation context.
     * @return {@code true} if no violations were detected, else {@code false}
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Start building a new database constraint violation. Note that the violation will only
     * be stored after invoking {@link DatabaseConstraintViolationBuilder#addViolation()}.
     * @param propertyRef reference to the property that was violated
     * @param templateName name of the message template
     * @return new violation builder
     */
    public ViolationBuilder buildViolationWithTemplate(PropertyReference propertyRef, String templateName) {
        return new ViolationBuilder(propertyRef, templateName);
    }
    
    public class ViolationBuilder {
        private final PropertyReference propertyReference;
        private ViolationMessageTemplate template;

        private ViolationBuilder(PropertyReference propertyReference, String templateName) {
            this.propertyReference = propertyReference;
            this.template = messageBuilder.template(templateName);
        }

        public ViolationBuilder value(Object value) {
            template.value(value);
            return this;
        }

        public ViolationBuilder attribute(String attribute, Object value) {
            template.attribute(attribute, value);
            return this;
        }

        // TODO: Does not support '.' separated paths, find a solution
        public DatabaseConstraintValidationContext addViolation() {
            validatorContext.buildConstraintViolationWithTemplate(template.message()).addNode(propertyReference.getName()).addConstraintViolation();
            markAsInvalid();
            return DatabaseConstraintValidationContext.this;
        }

        private void markAsInvalid() {
            valid = false;
        }
    }
    
}
