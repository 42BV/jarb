package org.jarbframework.populator.excel.metamodel;

import java.lang.reflect.Field;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jarbframework.utils.Asserts;

/**
 * Specific node in our path.
 */
public class PropertyNode {
    private final Field field;

    /**
     * Construct a new {@link 
     * @param field the field being represented
     */
    PropertyNode(Field field) {
        this.field = Asserts.notNull(field, "Field cannot be null");
    }

    /**
     * Retrieve the actual field.
     * @return field
     */
    public Field getField() {
        return field;
    }

    /**
     * Retrieve the property name.
     * @return property name
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Retrieve the property type.
     * @return property type
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Retrieve the class declaring our property.
     * @return property declaring class
     */
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
