package org.jarb.populator.excel.mapping.excelrow;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to return records from the objectmodel by relationships.
 * @author Willem Eppen
 * @author Sander Benschop
 *
 */
public final class ForeignExcelRowGrabber {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForeignExcelRowGrabber.class);

    /** Private constructor. */
    private ForeignExcelRowGrabber() {
    }

    /**
     * Returns a set with instance values of records on many-side or just one instance value depending on the multiplicity.
     * @param map Hashmap of Excelrecords and foreign keys
     * @return Set of excelRecords referenced to by foreignKeys
     */
    protected static Object getInstanceValue(Key key, Map<Integer, ExcelRow> map) {
        Object returnValue;
        if (key instanceof JoinTableKey) {
            returnValue = getInstancesByJoinTableKey(key, map);
        } else if (key instanceof JoinColumnKey) {
            Integer foreignKey = ((JoinColumnKey) key).getKeyValue();
            returnValue = getInstanceByJoinColumnKey(foreignKey, map);
        } else {
            LOGGER.warn("Not an instance of JoinTableKey or JoinColumnKey. Cannot retrieve data.");
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * Returns the foreign record for a JoinColumn relationship.
     * @param foreignKey Primary key of foreign record
     * @param map Map to search the foreign record in
     * @return Foreign record or null if not available
     */
    private static Object getInstanceByJoinColumnKey(Integer foreignKey, Map<Integer, ExcelRow> map) {
        Object returnValue = null;
        if (map != null && map.containsKey(foreignKey)) {
            returnValue = map.get(foreignKey).getCreatedInstance();
        } else {
            LOGGER.warn("Foreign record was not found. Ommiting value from field.");
        }
        return returnValue;
    }

    /**
     * Loops over the list of foreign keys and puts the instances the foreign key points to in the map.
     * @param key JoinTableKey used to get the instances
     * @param map Map with excelrows
     * @return Set of instances
     */
    private static Set<Object> getInstancesByJoinTableKey(Key key, Map<Integer, ExcelRow> map) {
        Set<Object> instances = new HashSet<Object>();
        Set<Integer> foreignKeys = ((JoinTableKey) key).getKeyValues();
        for (Integer foreignKey : foreignKeys) {
            if (map.containsKey(foreignKey)) {
                instances.add(map.get(foreignKey).getCreatedInstance());
            } else {
                LOGGER.warn("Foreign record was not found. Ommiting record from associative table.");
            }
        }
        return instances;
    }
}
