package org.jarb.constraint.database.column;

import javax.sql.DataSource;

import org.jarb.utils.bean.PropertyReference;
import org.jarb.utils.orm.ColumnReference;
import org.jarb.utils.orm.SchemaMapper;

/**
 * Retrieves the database constraints for a specific entity.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class EntityAwareColumnMetadataRepository {
    /** Retrieves column meta-data. **/
    private ColumnMetadataRepository columnMetadataRepository;
    /** Maps beans and properties to tables and columns. **/
    private SchemaMapper schemaMapper;

    /**
     * Construct a new {@link EntityAwareColumnMetadataRepository}.
     * @param dataSource describes how to connect to the database
     */
    public EntityAwareColumnMetadataRepository(DataSource dataSource) {
        this(new CachingColumnMetadataRepository(new JdbcColumnMetadataProvider(dataSource)));
    }

    /**
     * Construct a new {@link EntityAwareColumnMetadataRepository}.
     * @param columnMetadataRepository provided meta-data for a specific column
     */
    public EntityAwareColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    public void setSchemaMapper(SchemaMapper schemaMapper) {
        this.schemaMapper = schemaMapper;
    }

    /**
     * Retrieve the column constraint information for a specific property.
     * @param entityClass class of the entity
     * @param propertyName name of the property
     * @return constraint information of a specific property, or {@code null}
     * if no meta data could be retrieved.
     * @throws UnknownTableException if we could not map the entity to a table
     * @throws UnknownColumnException if we could not map the property to a column
     */
    public ColumnMetadata getColumnMetadata(Class<?> entityClass, String propertyName) {
        ColumnReference columnReference = schemaMapper.column(new PropertyReference(propertyName, entityClass));
        if (columnReference == null) {
            throw new UnknownColumnException("Could not resolve the column name of '" + propertyName + "' (" + entityClass.getSimpleName() + ")");
        }
        return columnMetadataRepository.getColumnMetadata(columnReference.getTableName(), columnReference.getColumnName());
    }
}
