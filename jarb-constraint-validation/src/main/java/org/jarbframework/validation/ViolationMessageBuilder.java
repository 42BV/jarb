package org.jarbframework.validation;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * Constructs constraint violation messages with enhanced flexibility. Message variables
 * can be defined manually, rather than extracting them from a constraint annotation. This
 * allows us to generate violation messages for property violations with no corresponding
 * constraint annotation.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
class ViolationMessageBuilder {
    private final MessageInterpolator messageInterpolator;

    /**
     * Construct a new {@link ViolationMessageBuilder}, using a custom message interpoulator.
     * @param messageInterpolator interpolator used to build violation messages
     */
    public ViolationMessageBuilder(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    /**
     * Builds a new message according to some template.
     * @param templateName name of the template
     * @return new message based on a template
     */
    public ViolationMessageTemplate template(String templateName) {
        return new ViolationMessageTemplate(templateName);
    }

    /**
     * Represents a message in a specific template. Variables can be defined as attributes,
     * and also the rejected property value can be configured. After all values have been
     * defined, the actual string message is retrieved by calling {@link #message()}.
     */
    public class ViolationMessageTemplate {
        private final Map<String, Object> attributes;
        private final String templateName;
        private Object value;

        /**
         * Construct a new {@link ViolationMessageTemplate}.
         * @param templateName name of the template
         */
        private ViolationMessageTemplate(String templateName) {
            this.templateName = templateName;
            attributes = new HashMap<String, Object>();
        }

        /**
         * Configure a new attribute value, which can be referenced inside the template.
         * @param attribute name of the attribute
         * @param value value that the attribute should have
         * @return same template instance, allowing method chaining
         */
        public ViolationMessageTemplate attribute(String attribute, Object value) {
            attributes.put(attribute, value);
            return this;
        }

        /**
         * Configure the value that was validated, which can be referenced inside the template.
         * @param value the rejected value
         * @return same template instance, allowing method chaining
         */
        public ViolationMessageTemplate value(Object value) {
            this.value = value;
            return this;
        }

        /**
         * Retrieve the actual violation message.
         * @return violation message
         */
        public String message() {
            AttributeConstraintDescriptor descriptor = new AttributeConstraintDescriptor(attributes);
            return messageInterpolator.interpolate(templateName, new SimpleInterpolatorContext(descriptor, value));
        }
    }

    /**
     * Simple interpolator context implementation that returns the configured values.
     */
    private static class SimpleInterpolatorContext implements MessageInterpolator.Context {
        private final ConstraintDescriptor<?> constraintDescriptor;
        private final Object value;

        public SimpleInterpolatorContext(ConstraintDescriptor<?> constraintDescriptor, Object value) {
            this.constraintDescriptor = constraintDescriptor;
            this.value = value;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return constraintDescriptor;
        }

        @Override
        public Object getValidatedValue() {
            return value;
        }
    }

    /**
     * Constraint descriptor implementation that only provides the configured attributes.
     * This adapter class is needed to enable usage of the message interpolator.
     */
    private static class AttributeConstraintDescriptor implements ConstraintDescriptor<Annotation> {
        private final Map<String, Object> attributes;

        public AttributeConstraintDescriptor(Map<String, Object> attributes) {
            this.attributes = unmodifiableMap(attributes);
        }

        @Override
        public Annotation getAnnotation() {
            return null;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public Set<ConstraintDescriptor<?>> getComposingConstraints() {
            return emptySet();
        }

        @Override
        public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
            return emptyList();
        }

        @Override
        public Set<Class<?>> getGroups() {
            return emptySet();
        }

        @Override
        public Set<Class<? extends Payload>> getPayload() {
            return emptySet();
        }

        @Override
        public boolean isReportAsSingleViolation() {
            return false;
        }
    }

}
