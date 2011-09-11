/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.utils.database;

/**
 * Thrown whenever we could not recognize the type of database.
 *
 * @author Jeroen van Schagen
 * @since Sep 8, 2011
 */
public class UnrecognizedDatabaseException extends RuntimeException {

    public UnrecognizedDatabaseException(String message) {
        super(message);
    }

}
