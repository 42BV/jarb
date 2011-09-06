package org.jarb.constraint.database.column;

import org.jarb.utils.orm.ColumnReference;
import org.springframework.util.Assert;

/**
 * Repository that loads all constraint meta-data in memory, minimizing database
 * connectivity. With a caching constraint repository, the retrieval of constraint
 * metadata should be significantly faster.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
public class CachingColumnMetadataRepository implements ColumnMetadataRepository {
    /** Maintains all constraint meta-data in memory **/
    private final MapColumnMetadataRepository columnMetadataCache = new MapColumnMetadataRepository();
    /** Capable of filling our cache with constraint meta-data **/
    private final ColumnMetadataProvider columnMetadataProvider;
    /** Describes whether our cache has been filled yet, or not **/
    private boolean metadataCached = false;

    /**
     * Construct a new {@link CachingColumnMetadataRepository}.
     * @param columnMetadataProvider provides column meta-data for our cache
     */
    public CachingColumnMetadataRepository(ColumnMetadataProvider columnMetadataProvider) {
        Assert.notNull(columnMetadataProvider, "Property 'column metadata provider' cannot be null");
        this.columnMetadataProvider = columnMetadataProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        checkCache(); // Ensure our cache is filled
        return columnMetadataCache.getColumnMetadata(columnReference);
    }

    /**
     * Cache the column metadata, whenever needed.
     */
    private synchronized void checkCache() {
        if (!metadataCached) {
            fillCache();
        }
    }

    /**
     * Retrieve all column metadata and store it inside our cache.
     */
    private void fillCache() {
        for (ColumnMetadata columnConstraint : columnMetadataProvider.all()) {
            columnMetadataCache.store(columnConstraint);
        }
        metadataCached = true;
    }

    /**
     * Remove all cached metdata. Clearing the cache might be desirable
     * whenever the table structure has changed during runtime.
     */
    public synchronized void clearCache() {
        columnMetadataCache.removeAll();
        metadataCached = false;
    }

}
