/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.constraint.metadata.database;

import nl._42.jarb.utils.bean.PropertyReference;
import nl._42.jarb.utils.orm.ColumnReference;
import nl._42.jarb.utils.orm.SchemaMapper;

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
