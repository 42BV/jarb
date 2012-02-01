package org.jarbframework.populator.excel.metamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract supertype of Definition classes. Holds the common properties and functions.
 * @author Sander Benschop
 *
 */
public abstract class Definition {

    /** Description of each defined property. */
    private Set<PropertyDefinition> properties;

    /**
     * Retrieve all property definitions declared inside this class.
     * @return definition of each declared property
     */
    public Set<PropertyDefinition> properties() {
        return Collections.unmodifiableSet(getProperties());
    }

    /**
     * Retrieve a specific property definition.
     * @param propertyName name of the property field
     * @return matching property field, if any
     */
    public PropertyDefinition property(String propertyName) {
        PropertyDefinition result = null;
        for (PropertyDefinition property : getProperties()) {
            if (StringUtils.equalsIgnoreCase(propertyName, property.getName())) {
                result = property;
            }
        }
        return result;
    }

    /**
     * Retrieve a specific property definition, based on property. Note
     * that the column must actually map to a property inside our class.
     * Certain columns, such as discriminators, are not mapped to a field
     * and thus will result in a {@link null}.
     * @param columnName name of the column
     * @return matching property field, if any
     */
    public PropertyDefinition propertyByColumnName(String columnName) {
        PropertyDefinition result = null;
        for (PropertyDefinition property : getProperties()) {
            if (StringUtils.equalsIgnoreCase(columnName, property.getColumnName())) {
                result = property;
            }
        }
        return result;
    }

    /**
     * Retrieves the field name by a passed column name.
     * @param columnName Column name that belongs to a field.
     * @return Field name
     */
    public String fieldNameByColumnName(String columnName) {
        return propertyByColumnName(columnName).getField().getName();
    }

    /**
     * Retrieve all column names for this entity table.
     * @return each column name on this entity
     */
    public Set<String> getColumnNames() {
        Set<String> columnNames = new HashSet<String>();
        for (PropertyDefinition property : getProperties()) {
            if (property.hasColumn()) {
                columnNames.add(property.getColumnName());
            }
        }
        return columnNames;
    }

    /**
     * Returns the Set of PropertyDefinitions.
     * @return Set of PropertyDefinitions
     */
    public Set<PropertyDefinition> getProperties() {
        return properties;
    }

    /**
     * Sets the Set of PropertyDefinitions.
     * @param properties PropertyDefinitions to set for Definition
     */
    public void setProperties(Set<PropertyDefinition> propertyDefinitions) {
        this.properties = propertyDefinitions;
    }

    /**
     * Capable of building {@link EntityDefinition} instances.
     * 
     * @author Jeroen van Schagen
     * @since 15-06-2011
     *
     * @param <T> type of class being described
     */
    public abstract static class Builder<T> {
        private Set<PropertyDefinition> properties = new HashSet<PropertyDefinition>();

        /**
         * Include a column definition.
         * @param properties property definitions being added
         * @return this for method chaining
         */
        public Builder<T> includeProperties(Collection<PropertyDefinition> propertyDefinitions) {
            this.getProperties().addAll(propertyDefinitions);
            return this;
        }

        /**
         * Returns the Set of PropertyDefinitions.
         * @return Set of PropertyDefinitions
         */
        public Set<PropertyDefinition> getProperties() {
            return properties;
        }

    }

}
