/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

/**
 * Maps bean (properties) to database tables and columns.
 *
 * @author Jeroen van Schagen
 * @date Aug 16, 2011
 */
public interface SchemaMapper {

    /**
     * Retrieve the table name of a entity.
     * @param entityClass type of entity
     * @return name of the table
     */
    String table(Class<?> entityClass);

    /**
     * Retrieve the column that a property maps to.
     * @param entityClass type of entity that holds the property
     * @param propertyName name of the property
     * @return reference to the column, if any
     */
    ColumnReference column(Class<?> entityClass, String propertyName);

}
