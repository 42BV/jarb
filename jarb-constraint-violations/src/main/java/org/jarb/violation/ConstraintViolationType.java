package org.jarb.violation;

/**
 * Type of constraint violations that can occur.
 * 
 * @author Jeroen van Schagen
 * @since 16-05-2011
 */
public enum ConstraintViolationType {

    /**
     * Unique key already exists in database.
     */
    UNIQUE,

    /**
     * Foreign key reference does not exist.
     */
    FOREIGN_KEY,

    /**
     * Null value was not accepted for column.
     */
    NOT_NULL,

    /**
     * Expression type does not match column.
     */
    INVALID_TYPE,

    /**
     * Value length is larger than column maximum size.
     */
    LENGTH_EXCEEDED,

    /**
     * Check constraint was not satisfied.
     */
    CHECK_FAILED

}
