package org.jarb.constraint.database.named;

/**
 * Type of named constraints that can be declared in a database.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public enum NamedConstraintType {

    /**
     * Identifies the table row, has to be unique and not null.
     */
    PRIMARY_KEY,

    /**
     * Can only be used once inside a table.
     */
    UNIQUE_INDEX,

    /**
     * Custom constraint logic has to be satisfied.
     */
    CHECK

}
