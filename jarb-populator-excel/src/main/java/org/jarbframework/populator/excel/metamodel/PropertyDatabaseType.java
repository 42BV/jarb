package org.jarbframework.populator.excel.metamodel;

/**
 * Type of "column" we can have in our mapping.
 * @author Jeroen van Schagen
 */
public enum PropertyDatabaseType {

    /**
     * Regular column, contains a value.
     */
    COLUMN,

    /**
     * References to another entity.
     */
    REFERENCE,

    /**
     * References to one or more other entities by an associative
     * table. For example, we have a table "users" and "roles" and
     * an associative table "user_roles" that couples them together.
     */
    COLLECTION_REFERENCE,

    /**
     * An ElementCollection mapping can be used to define a collection of Embeddable objects. 
     * This is not a typical usage of Embeddable objects as the objects are not embedded in 
     * the source object's table, but stored in a separate collection table. 
     */
    ELEMENT_COLLECTION;
}
