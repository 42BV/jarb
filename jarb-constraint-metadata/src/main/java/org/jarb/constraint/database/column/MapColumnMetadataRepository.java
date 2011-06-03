package org.jarb.constraint.database.column;

import static org.apache.commons.lang.StringUtils.lowerCase;

import java.util.HashMap;
import java.util.Map;


/**
 * Map based column constraint repository. Matching is made case insensitve by
 * storing, and searching, based on lowercased character sequences.
 * 
 * @author Jeroen van Schagen
 * @since 30-05-2011
 */
public class MapColumnMetadataRepository implements ColumnMetadataRepository {
    private final Map<String, Map<String, ColumnMetadata>> contentMap;

    /**
     * Construct a new {@link MapColumnMetadataRepository}.
     */
    public MapColumnMetadataRepository() {
        contentMap = new HashMap<String, Map<String, ColumnMetadata>>();
    }

    /**
     * Store a column constraint inside this repository.
     * @param columnConstraint column constraint that should be added
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository store(ColumnMetadata columnConstraint) {
        Map<String, ColumnMetadata> columnsConstraintsMap = contentMap.get(lowerCase(columnConstraint.getTableName()));
        if (columnsConstraintsMap == null) {
            columnsConstraintsMap = new HashMap<String, ColumnMetadata>();
            contentMap.put(lowerCase(columnConstraint.getTableName()), columnsConstraintsMap);
        }
        columnsConstraintsMap.put(lowerCase(columnConstraint.getColumnName()), columnConstraint);
        return this;
    }

    /**
     * Remove all stored constraints from this repository.
     * @return the same repository instance, for chaining
     */
    public MapColumnMetadataRepository removeAll() {
        contentMap.clear();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnMetadata getColumnMetadata(String tableName, String columnName) {
        ColumnMetadata columnMetadata = null;
        if (contentMap.containsKey(lowerCase(tableName))) {
            columnMetadata = contentMap.get(lowerCase(tableName)).get(lowerCase(columnName));
        }
        return columnMetadata;
    }

}
