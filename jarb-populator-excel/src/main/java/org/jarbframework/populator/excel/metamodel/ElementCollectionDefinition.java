package org.jarbframework.populator.excel.metamodel;

import java.util.Collections;

import org.springframework.util.Assert;

/**
 * Describes a specific persistable ElementCollection class, providing additional information about
 * the class. The mapping to a database table is provided in the enclosing class.
 * @author Sander Benschop
 *
 * @param <T>
 */
public class ElementCollectionDefinition<T> extends Definition {

    /** The entity class the elementCollection is enclosing in. */
    private final Class<T> enclosingClass;

    /** Entity class being described. */
    protected final Class<T> definedClass;
    
    /**
     * Construct a new {@link ElementCollectionDefinition).
     * @param definedClass class being described
     */
    private ElementCollectionDefinition(Class<T> entityClass, Class<T> enclosingClass) {
        this.definedClass = entityClass;
        this.enclosingClass = enclosingClass;

    }

    /**
     * Returns the definedClass belonging to classDefinition.
     * @return definedClass instance from domain package
     */
    public Class<T> getDefinedClass() {
        return definedClass;
    }
    
    /**
     * Start building a new {@link ElementCollectionDefinition}.
     * @param <T> type of class being described
     * @param elementCollectionClass being described
     * @return class definition builder
     */
    public static <T> Builder<T> forClass(Class<T> entityClass) {
        return new ElementCollectionDefinition.Builder<T>(entityClass);
    }

    /**
     * Returns the enclosing class.
     * @return Enclosing class
     */
    public Class<T> getEnclosingClass() {
        return enclosingClass;
    }

    public static class Builder<T> extends Definition.Builder<T> {

        private Class<T> definedClass;
        
        /** The entity class the elementCollection is enclosing in. */
        private Class<T> enclosingClass;

        /**
         * Construct a new {@link Builder}.
         * @param elementCollectionClass class being described
         */
        public Builder(Class<T> elementCollectionClass) {
            this.definedClass = elementCollectionClass;
        }

        /**
         * Construct a new class definition that contains all previously configured attributes.
         * @return new class definition
         */
        public ElementCollectionDefinition<T> build() {
            Assert.hasText(tableName, "Table name cannot be blank");

            ElementCollectionDefinition<T> elementCollectionDefinition = new ElementCollectionDefinition<T>(definedClass, enclosingClass);
            elementCollectionDefinition.tableName = tableName;
            elementCollectionDefinition.propertyDefinitions = Collections.unmodifiableSet(properties);
            return elementCollectionDefinition;
        }

        /**
         * Describe the table name of our class.
         * @param tableName name of the table
         * @return this for method chaining
         */
        public Builder<T> setTableName(final String tableName) {
            this.tableName = tableName;
            return this;
        }

        /**
         * The class the ElementCollection is defined in.
         * @param enclosingClass The class the ElementCollection is enclosed in.
         * @return this for method chaining
         */
        public Builder<T> setEnclosingClass(final Class<T> enclosingClass) {
            this.enclosingClass = enclosingClass;
            return this;
        }

    }
}
