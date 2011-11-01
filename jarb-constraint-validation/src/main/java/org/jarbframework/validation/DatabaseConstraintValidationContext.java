package org.jarbframework.validation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.validation.ViolationMessageBuilder.ViolationMessageTemplate;

/**
 * Context of a specific bean validation. Bean violations can be
 * retrieved and included to this context.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
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
     * be stored after invoking {@link DatabaseConstraintViolationBuilder#addToContext()}.
     * @param propertyRef reference to the property that was violated
     * @param templateName name of the message template
     * @return new violation builder
     */
    public ViolationBuilder buildViolationWithTemplate(PropertyReference propertyRef, String templateName) {
        return new ViolationBuilder(propertyRef, templateName);
    }

    /**
     * Used to build and include a new constraint violation to our context.
     */
    public class ViolationBuilder {
        /** Reference to the property that was rejected **/
        private final PropertyReference propertyReference;
        /** Template of the violation message that should be supplied **/
        private ViolationMessageTemplate template;

        private ViolationBuilder(PropertyReference propertyReference, String templateName) {
            this.propertyReference = propertyReference;
            this.template = messageBuilder.template(templateName);
        }

        /**
         * Configure an attribute value inside the message template.
         * @param attribute name of the attribute
         * @param value the attribute value that should be used
         * @return this builder instance
         */
        public ViolationBuilder attribute(String attribute, Object value) {
            template.attribute(attribute, value);
            return this;
        }

        /**
         * Configure the rejected property value, usable inside our message template.
         * @param value the rejected property value
         * @return this builder instance
         */
        public ViolationBuilder value(Object value) {
            template.value(value);
            return this;
        }

        /**
         * Finish building and include the violation to our context.
         * @return the relevant context, capable of building new violation
         */
        public DatabaseConstraintValidationContext addToContext() {
            markAsInvalid();
            if (propertyReference.isNestedProperty()) {
                buildAndIncludeNestedPropertyViolation();
            } else {
                buildConstraintViolation().addNode(propertyReference.getName()).addConstraintViolation();
            }
            return DatabaseConstraintValidationContext.this;
        }

        private void markAsInvalid() {
            valid = false;
        }

        private void buildAndIncludeNestedPropertyViolation() {
            String[] path = propertyReference.getPath();
            NodeBuilderDefinedContext rootContext = buildConstraintViolation().addNode(path[0]);
            NodeBuilderCustomizableContext nestedContext = rootContext.addNode(path[1]);
            for (int i = 2; i < path.length; i++) {
                nestedContext = nestedContext.addNode(path[i]);
            }
            nestedContext.addConstraintViolation();
        }

        private ConstraintViolationBuilder buildConstraintViolation() {
            return validatorContext.buildConstraintViolationWithTemplate(template.message());
        }
    }
}
