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
    UNIQUE_VIOLATION,

    /**
     * Null value was not accepted for column.
     */
    CANNOT_BE_NULL,

    /**
     * Foreign key reference does not exist.
     */
    INVALID_REFERENCE,

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
