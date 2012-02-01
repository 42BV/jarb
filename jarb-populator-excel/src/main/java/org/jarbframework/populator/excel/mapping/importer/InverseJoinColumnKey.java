package org.jarbframework.populator.excel.mapping.importer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Key for an inversed JoinColumn relation, could be used for an ElementCollection relation.
 * Could also be a @OneToMany with @JoinColumn annotation
 * @author benschop
 *
 */
public class InverseJoinColumnKey extends Key {

    /** Map with JoinColumn names as keys and objects as values.  */
    private Map<String, Object> foreignKeys;

    /** TableName is only needed if it is used for a relation with an ElementCollection of a serializable type.*/
    private String serializableTypeElementCollectionTableName;

    /**
     * Sets the foreign key HashMap.
     * @param foreignKeyMap HashMap with JoinColumn names as keys and objects as values
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setKeyValue(Object foreignKeyMap) {
        Assert.isInstanceOf(Map.class, foreignKeyMap, "Key passed to InverseJoinColumnKey is not of type Hashmap.");
        this.foreignKeys = (Map<String, Object>) foreignKeyMap;
    }

    /**
     * Returns the foreign key values.
     * @return Map with JoinColumn names as keys and objects as values
     */
    public Map<String, Object> getKeyValues() {
        return foreignKeys;
    }

    /**
     * Sets the tableName.
     * Only needed for an ElementCollection of a serializable type.
     * @param tableName Table name
     */
    public void setTableName(String tableName) {
        this.serializableTypeElementCollectionTableName = tableName;
    }

    /**
     * Returns the tableName.
     * Only needed for an ElementCollection of a serializable type.
     * @return Table name for ElementCollection of a serializable type
     */
    public String getTableName() {
        return serializableTypeElementCollectionTableName;
    }

}
