/*
 * (C) 2011 Nidera (www.nidera.com). All rights reserved.
 */
package org.jarb.constraint.database;

import org.jarb.constraint.database.column.ColumnMetadata;
import org.jarb.constraint.database.column.ColumnMetadataRepository;
import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.ColumnReference;
import org.jarb.utils.orm.SchemaMapper;

/**
 * DatabaseConstraintRepository
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
     * @return constraint of the column, or {@code null}
     */
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        return columnMetadataRepository.getColumnMetadata(columnReference);
    }

    /**
     * Retrieve the meta-data of a specific database column.
     * @param beanClass class of the bean containing our property
     * @param propertyName name of the property
     * @return constraint of the column, or {@code null}
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    public ColumnMetadata getColumnMetadata(Class<?> beanClass, String propertyName) {
        return getColumnMetadata(new PropertyReference(beanClass, propertyName));
    }

    /**
     * Retrieve the meta-data of a specific database column.
     * @param columnReference reference to a column
     * @return constraint of the column, or {@code null}
     * @throws CouldNotBeMappedToColumnException whenever some property could not be mapped to a column
     */
    public ColumnMetadata getColumnMetadata(PropertyReference propertyReference) {
        ColumnReference columnReference = schemaMapper.column(propertyReference);
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
