package org.jarb.constraint.database.named;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Metadata of a named database constraint.
 * 
 * @author Jeroen van Schagen
 * @since 6-6-2011
 */
public class NamedConstraintMetadata {
    private final String name;
    private final NamedConstraintType type;

    /**
     * Construct a new {@link NamedConstraintMetadata}.
     * @param name constraint name
     * @param type constraint type
     */
    public NamedConstraintMetadata(String name, NamedConstraintType type) {
        Assert.hasText(name);
        this.name = name;
        this.type = type;
    }

    /**
     * Retrieve the constraint name.
     * @return constraint name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the constraint type.
     * @return constraint type
     */
    public NamedConstraintType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof NamedConstraintMetadata) {
            equals = name.equals(((NamedConstraintMetadata) obj).getName());
        }
        return equals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
