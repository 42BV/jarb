package org.jarb.constraint.database.column;

/**
 * Provides access to the metadata of a specific database column.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public interface ColumnMetadataRepository {

    /**
     * Retrieve the metadata of a specific database column.
     * @param tableName name of the table that contains our column (case insensitive)
     * @param columnName name of the column (case insensitive)
     * @return constraint of the column, or {@code null}
     */
    ColumnMetadata getColumnMetadata(String tableName, String columnName);

}
