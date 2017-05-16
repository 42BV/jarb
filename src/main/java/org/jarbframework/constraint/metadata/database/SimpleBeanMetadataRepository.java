/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.constraint.metadata.database;

import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.ColumnReference;
import org.jarbframework.utils.orm.SchemaMapper;

public class SimpleBeanMetadataRepository implements BeanMetadataRepository {
    
    private final ColumnMetadataRepository columnMetadataRepository;

    private final SchemaMapper schemaMapper;
    
    public SimpleBeanMetadataRepository(ColumnMetadataRepository columnMetadataRepository, SchemaMapper schemaMapper) {
        this.columnMetadataRepository = columnMetadataRepository;
        this.schemaMapper = schemaMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnMetadata getColumnMetadata(PropertyReference propertyReference) {
        ColumnMetadata columnMetadata = null;
        ColumnReference columnReference = schemaMapper.getColumnReference(propertyReference);
        if (columnReference != null) {
            columnMetadata = columnMetadataRepository.getMetadata(columnReference);
        }
        return columnMetadata;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmbeddable(Class<?> propertyType) {
        return schemaMapper.isEmbeddable(propertyType);
    }
    
}
