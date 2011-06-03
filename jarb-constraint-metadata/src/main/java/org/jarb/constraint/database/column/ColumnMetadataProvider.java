package org.jarb.constraint.database.column;

import java.util.List;


/**
 * Capable of retrieving all column constraint metadata.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public interface ColumnMetadataProvider {

    /**
     * Retrieve all column constraint metadata.
     * @return all column constraint metadata
     */
    List<ColumnMetadata> all();

}
