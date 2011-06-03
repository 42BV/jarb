package org.jarb.constraint.database.column;

import org.springframework.util.Assert;

/**
 * Repository that loads all constraint metadata in memory, minimizing database
 * connectivity. With a caching constraint repository, the retrieval of constraint
 * metadata should be significantly faster.
 * 
 * @author Jeroen van Schagen
 * @since 24-05-2011
 */
public class CachingColumnMetadataRepository implements ColumnMetadataRepository {
    /** Maintains all constraint metadata in memory **/
    private final MapColumnMetadataRepository columnMetadataCache = new MapColumnMetadataRepository();
    /** Capable of filling our cache with constraint metadata **/
    private final ColumnMetadataProvider columnMetadataProvider;
    /** Describes whether our cache has been filled yet, or not **/
    private boolean metadataCached = false;

    /**
     * Construct a new {@link CachingColumnMetadataRepository}.
     * @param constraintsProvider provides column medata for our cache
     */
    public CachingColumnMetadataRepository(ColumnMetadataProvider constraintsProvider) {
        Assert.notNull(constraintsProvider, "Property 'constraints provider' cannot be null");
        this.columnMetadataProvider = constraintsProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized ColumnMetadata getColumnMetadata(String tableName, String columnName) {
        checkCache(); // Ensure our cache is filled
        return columnMetadataCache.getColumnMetadata(tableName, columnName);
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
