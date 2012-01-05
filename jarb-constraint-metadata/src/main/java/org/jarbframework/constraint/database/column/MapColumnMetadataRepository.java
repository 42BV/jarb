package org.jarbframework.constraint.database.column;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.jarbframework.utils.orm.ColumnReference;
import org.springframework.util.Assert;

/**
 * Map based column constraint repository. Matching is made case insensitve by
 * storing, and searching, based on lowercased character sequences.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class MapColumnMetadataRepository implements ColumnMetadataRepository {

    private final Map<String, Map<String, ColumnMetadata>> columnMetadataMap;

    /**
     * Construct a new {@link MapColumnMetadataRepository}.
     */
    public MapColumnMetadataRepository() {
        columnMetadataMap = new HashMap<String, Map<String, ColumnMetadata>>();
    }

    /**
     * Store a column constraint inside this repository.
     * @param columnMetadata column constraint that should be added
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository store(ColumnMetadata columnMetadata) {
        Assert.notNull(columnMetadata, "Cannot store null pointer column metadata");
        Map<String, ColumnMetadata> tableSpecificColumnMetadataMap = columnMetadataMap.get(lowerCase(columnMetadata.getTableName()));
        if (tableSpecificColumnMetadataMap == null) {
            tableSpecificColumnMetadataMap = new HashMap<String, ColumnMetadata>();
            columnMetadataMap.put(lowerCase(columnMetadata.getTableName()), tableSpecificColumnMetadataMap);
        }
        tableSpecificColumnMetadataMap.put(lowerCase(columnMetadata.getColumnName()), columnMetadata);
        return this;
    }

    /**
     * Remove all stored constraints from this repository.
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository removeAll() {
        columnMetadataMap.clear();
        return this;
    }

    @Override
    public ColumnMetadata getColumnMetadata(ColumnReference columnReference) {
        ColumnMetadata columnMetadata = null;
        if (columnMetadataMap.containsKey(lowerCase(columnReference.getTableName()))) {
            columnMetadata = columnMetadataMap.get(lowerCase(columnReference.getTableName())).get(lowerCase(columnReference.getColumnName()));
        }
        return columnMetadata;
    }

}
