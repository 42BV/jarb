/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.constraint.database;

import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.ColumnReference;

/**
 * Provides access to the constraint meta-data of a database.
 *
 * @author Jeroen van Schagen
 * @date Sep 6, 2011
 */
public interface DatabaseConstraintRepository {

    /**
     * Retrieve the meta-data of a specific database column.
     * @param beanClass class of the bean containing our property
     * @param propertyName name of the property
     * @return description of the column, if any
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    ColumnMetadata getColumnMetadata(Class<?> beanClass, String propertyName);

    /**
     * Retrieve the meta-data of a specific database column.
     * @param propertyReference reference to a property
     * @return description of the column, if any
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    ColumnMetadata getColumnMetadata(PropertyReference propertyReference);

    /**
     * Retrieve the meta-data of a specific database column.
     * @param columnReference reference to a column
     * @return description of the column, if any
     */
    ColumnMetadata getColumnMetadata(ColumnReference columnReference);

}
