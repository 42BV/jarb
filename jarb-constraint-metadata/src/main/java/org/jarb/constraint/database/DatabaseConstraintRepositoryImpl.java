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
 * Default implementation of {@link DatabaseConstraintRepository}.
 *
 * @author Jeroen van Schagen
 * @date Sep 6, 2011
 */
public class DatabaseConstraintRepositoryImpl implements DatabaseConstraintRepository {

    /** Retrieves column meta-data. **/
    private ColumnMetadataRepository columnMetadataRepository;

    /** Maps beans and properties to tables and columns. **/
    private SchemaMapper schemaMapper;

    @Override
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        return columnMetadataRepository.getColumnMetadata(columnReference);
    }

    @Override
    public ColumnMetadata getColumnMetadata(Class<?> beanClass, String propertyName) {
        return getColumnMetadata(new PropertyReference(beanClass, propertyName));
    }

    @Override
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
