package org.jarbframework.validation;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.validation.ViolationMessageBuilder.ViolationMessageTemplate;

/**
 * Result of a database constraint validation.
 * 
 * @author Jeroen van Schagen
 * @since 19-10-2011
 * 
 * @see DatabaseConstraintValidator
 */
public final class DatabaseConstraintValidation {
    private final List<DatabaseConstraintViolation> violations = new ArrayList<DatabaseConstraintViolation>();
    private final ViolationMessageBuilder messageBuilder;

    DatabaseConstraintValidation(ViolationMessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    /**
     * Start building a new database constraint violation. Note that the violation will only
     * be stored after invoking {@link DatabaseConstraintViolationBuilder#addViolation()}.
     * @param propertyRef reference to the property that was violated
     * @param templateName name of the message template
     * @return new violation builder
     */
    public DatabaseConstraintViolationBuilder buildViolationWithTemplate(PropertyReference propertyRef, String templateName) {
        return new DatabaseConstraintViolationBuilder(propertyRef, templateName);
    }

    /**
     * Retrieve an unmodifiable list of all stored constraint violations.
     * @return all constraint violations
     */
    public List<DatabaseConstraintViolation> getViolations() {
        return unmodifiableList(violations);
    }

    /**
     * Determine if the bean has no violations.
     * @return {@code true} when no violations are found, else {@code false}
     */
    public boolean isValid() {
        return violations.isEmpty();
    }

    public class DatabaseConstraintViolationBuilder {
        private final PropertyReference propertyRef;
        private ViolationMessageTemplate template;

        private DatabaseConstraintViolationBuilder(PropertyReference propertyRef, String templateName) {
            this.propertyRef = propertyRef;
            this.template = messageBuilder.template(templateName);
        }

        public DatabaseConstraintViolationBuilder value(Object value) {
            template.value(value);
            return this;
        }

        public DatabaseConstraintViolationBuilder attribute(String attribute, Object value) {
            template.attribute(attribute, value);
            return this;
        }

        public DatabaseConstraintValidation addViolation() {
            violations.add(new DatabaseConstraintViolation(propertyRef, template.message()));
            return DatabaseConstraintValidation.this;
        }
    }

    public static final class DatabaseConstraintViolation {
        private final PropertyReference propertyRef;
        private final String message;

        public DatabaseConstraintViolation(PropertyReference propertyRef, String message) {
            this.propertyRef = propertyRef;
            this.message = message;
        }

        public PropertyReference getPropertyRef() {
            return propertyRef;
        }

        public String getMessage() {
            return message;
        }
    }
}