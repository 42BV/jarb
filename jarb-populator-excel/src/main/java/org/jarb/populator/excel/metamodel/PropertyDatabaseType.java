package org.jarb.populator.excel.metamodel;

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
    JOIN_COLUMN,
    
    /**
     * References to one or more other entities by an associative
     * table. For example, we have a table "users" and "roles" and
     * an associative table "user_roles" that couples them together.
     */
    JOIN_TABLE;
    
}
