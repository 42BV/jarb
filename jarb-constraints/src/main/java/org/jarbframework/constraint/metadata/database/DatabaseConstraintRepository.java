/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarbframework.constraint.metadata.database;

import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;

/**
 * Provides access to the constraint meta-data of a database.
 *
 * @author Jeroen van Schagen
 * @date Sep 6, 2011
 */
public class DatabaseConstraintRepository {

    /** Retrieves column meta-data. **/
    private ColumnMetadataRepository columnMetadataRepository;

    /** Maps beans and properties to tables and columns. **/
    private SchemaMapper schemaMapper;

    /**
     * Retrieve the meta-data of a specific database column.
     * @param columnReference reference to a column
     * @return description of the column, if any
     */
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        return columnMetadataRepository.getColumnMetadata(columnReference);
    }

    /**
     * Retrieve the meta-data of a specific database column.
     * @param beanClass class of the bean containing our property
     * @param propertyName name of the property
     * @return description of the column, if any
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    public ColumnMetadata getColumnMetadata(Class<?> beanClass, String propertyName) {
        return getColumnMetadata(new PropertyReference(beanClass, propertyName));
    }

    /**
     * Retrieve the meta-data of a specific database column.
     * @param propertyReference reference to a property
     * @return description of the column, if any
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    public ColumnMetadata getColumnMetadata(PropertyReference propertyReference) {
        ColumnReference columnReference = schemaMapper.columnOf(propertyReference);
        if (columnReference == null) {
            throw new CouldNotBeMappedToColumnException("Property '" + propertyReference + "' could not be mapped to a column.");
        }
        return getColumnMetadata(columnReference);
    }

    public void setColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

}
