package org.jarb.constraint.database.column;


/**
 * Retrieves the database constraints for a specific entity.
 * 
 * @author Jeroen van Schagen
 * @since 20-05-2011
 */
public class EntityAwareColumnMetadataRepository {
    /** Retrieves column metadata. **/
    private ColumnMetadataRepository columnMetadataRepository;
    /** Maps beans and properties to tables and columns. **/
    private TableMapper tableMapper = new JpaTableMapper();

    /**
     * Construct a new {@link EntityAwareColumnMetadataRepository}.
     * @param columnMetadataRepository provides the database constraints for a specific column
     */
    public EntityAwareColumnMetadataRepository(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    /**
     * Configure the table mapper that should be used to link beans and
     * properties to the corresponding table and columns.
     * @param tableMapper table mapper that should be used
     */
    public void setTableMapper(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
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
        String tableName = tableMapper.getTableName(entityClass);
        if (tableName == null) {
            throw new UnknownTableException("Could not resolve the table name of '" + entityClass.getSimpleName() + "'");
        }
        String columnName = tableMapper.getColumnName(entityClass, propertyName);
        if (columnName == null) {
            throw new UnknownColumnException("Could not resolve the column name of '" + propertyName + "' (" + entityClass.getSimpleName() + ")");
        }
        return columnMetadataRepository.getColumnMetadata(tableName, columnName);
    }
}
