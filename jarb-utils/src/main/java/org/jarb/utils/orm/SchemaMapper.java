/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.utils.orm;

import org.jarb.utils.bean.PropertyReference;

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
     * @throws NotAnEntityException whenever the specified bean
     * class could not be recognized as entity.
     */
    String table(Class<?> entityClass);

    /**
     * Retrieve the column that a property maps to, if any.
     * @param propertyReference property reference
     * @return column reference, if any
     * @throws NotAnEntityException whenever the specified bean
     * class could not be recognized as entity.
     */
    ColumnReference column(PropertyReference propertyReference);

}
