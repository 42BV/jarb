package nl._42.jarb.constraint.validation;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import nl._42.jarb.constraint.validation.ViolationMessageBuilder.ViolationMessageTemplate;
import nl._42.jarb.utils.bean.PropertyReference;

/**
 * Context of a specific bean validation. Bean violations can be retrieved and included to this context.
 *
 * @author Jeroen van Schagen
 * @since 20-10-2011
 */
final class DatabaseValidationContext {

    /** Context of the global bean validation. **/
    private final ConstraintValidatorContext context;

    /** Used to build database constraint violation messages. **/
    private final ViolationMessageBuilder messageBuilder;

    /** Describes if this context is still valid, meaning it has no violations. **/
    private boolean valid = true;

    DatabaseValidationContext(ConstraintValidatorContext context, ViolationMessageBuilder messageBuilder) {
        this.context = context;
        this.messageBuilder = messageBuilder;
        
        // Only include custom violation messages
        context.disableDefaultConstraintViolation();
    }

    /**
     * Determine if no violations were detected in this validation context.
     * 
     * @return {@code true} if no violations were detected, else {@code false}
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Start building a new database constraint violation. Note that the violation will only
     * be stored after invoking {@link ViolationBuilder#addToContext()}.
     * 
     * @param propertyReference reference to the property that was violated
     * @param templateName name of the message template
     * @return new database constraint violation builder
     */
    public ViolationBuilder buildViolationWithTemplate(PropertyReference propertyReference, String templateName) {
        return new ViolationBuilder(propertyReference, templateName);
    }

    /**
     * Used to build and include a new constraint violation to our context.
     */
    public class ViolationBuilder {

        /** Reference to the property that was rejected **/
        private final PropertyReference propertyReference;

        /** Template of the violation message that should be supplied **/
        private ViolationMessageBuilder.ViolationMessageTemplate template;

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
        public DatabaseValidationContext addToContext() {
            markAsInvalid();

            ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(template.message());
            addPropertyNode(builder).addConstraintViolation();

            return DatabaseValidationContext.this;
        }

        private void markAsInvalid() {
            valid = false;
        }

        private NodeBuilderCustomizableContext addPropertyNode(ConstraintViolationBuilder builder) {
            if (propertyReference.isNestedProperty()) {
                return builder.addPropertyNode(propertyReference.getPropertyName());
            } else {
                String[] path = propertyReference.getPath();
                NodeBuilderCustomizableContext context = builder.addPropertyNode(path[0]);
                for (int i = 1; i < path.length; i++) {
                    context = context.addPropertyNode(path[i]);
                }
                return context;
            }
        }

    }

}
