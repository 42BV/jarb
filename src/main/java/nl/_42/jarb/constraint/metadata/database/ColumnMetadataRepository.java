package nl._42.jarb.constraint.metadata.database;

import nl._42.jarb.utils.orm.ColumnReference;

/**
 * Provides access to the meta-data of a specific database column.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public interface ColumnMetadataRepository {

    /**
     * Retrieve the meta-data of a specific database column.
     * 
     * @param columnReference reference to a column
     * @return constraint of the column, or {@code null}
     */
    ColumnMetadata getMetadata(ColumnReference columnReference);

}
