/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

/**
 * Exception thrown when the specified bean class is not an entity.
 *
 * @author Jeroen van Schagen
 * @date Aug 17, 2011
 */
public class NotAnEntityException extends RuntimeException {

    public NotAnEntityException(String message) {
        super(message);
    }

}
