package org.jarb.constraint.database.named;

import static org.jarbframework.utils.Conditions.hasText;
import static org.jarbframework.utils.Conditions.notNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Named constraint description.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class NamedConstraintMetadata {
    private final String name;
    private final NamedConstraintType type;

    public NamedConstraintMetadata(String name, NamedConstraintType type) {
        this.name = hasText(name, "Constraint name cannot be empty.");
        this.type = notNull(type, "Constraint type cannot be null.");
    }

    public String getName() {
        return name;
    }

    public NamedConstraintType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
