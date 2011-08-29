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
     */
    String table(Class<?> entityClass);

    /**
     * Retrieve the column that a property maps to.
     * @param propertyReference property reference
     * @return column reference, if any
     */
    ColumnReference column(PropertyReference propertyReference);

}
