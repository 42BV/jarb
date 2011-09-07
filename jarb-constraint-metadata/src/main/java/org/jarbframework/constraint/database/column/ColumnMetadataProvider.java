package org.jarbframework.constraint.database.column;

import java.util.Set;

/**
 * Capable of retrieving all column metadata.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public interface ColumnMetadataProvider {

    /**
     * Retrieve the metadata for all columns inside our database.
     * @return all column metadata
     */
    Set<ColumnMetadata> all();

}
