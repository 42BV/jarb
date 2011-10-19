package org.jarbframework.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

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

        public DatabaseConstraintValidationContext addViolation() {
            markAsInvalid();
            if(propertyReference.isNestedProperty()) {
                addNestedPropertyViolation();
            } else {
                addSimpleViolation();
            }
            return DatabaseConstraintValidationContext.this;
        }

        private void markAsInvalid() {
            valid = false;
        }

        private void addNestedPropertyViolation() {
            String[] path = propertyReference.getPath();
            NodeBuilderDefinedContext rootContext = createConstraintViolationBuilder().addNode(path[0]);
            NodeBuilderCustomizableContext nestedContext = rootContext.addNode(path[1]);
            for(int i = 2; i < path.length; i++) {
                nestedContext = nestedContext.addNode(path[i]);
            }
            nestedContext.addConstraintViolation();
        }
        
        private ConstraintValidatorContext addSimpleViolation() {
            return createConstraintViolationBuilder().addNode(propertyReference.getName()).addConstraintViolation();
        }
        
        private ConstraintViolationBuilder createConstraintViolationBuilder() {
            return validatorContext.buildConstraintViolationWithTemplate(template.message());
        }
    }
}
