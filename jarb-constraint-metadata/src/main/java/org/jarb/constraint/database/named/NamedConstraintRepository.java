/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.constraint.database.named;

/**
 * Provides information about named constraints inside a database.
 *
 * @author Jeroen van Schagen
 * @date Sep 7, 2011
 */
public interface NamedConstraintRepository {

    /**
     * Describes a named constraint.
     * @param constraintName name of the constraint
     * @return named constraint description
     */
    NamedConstraintMetadata describe(String constraintName);

}
