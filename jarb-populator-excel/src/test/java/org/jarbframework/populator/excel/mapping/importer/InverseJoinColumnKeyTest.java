package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.junit.Before;
import org.junit.Test;

public class InverseJoinColumnKeyTest extends DefaultExcelTestDataCase {

    private Key key;

    @Before
    public void setupInverseJoinColumnKey() {
        key = new InverseJoinColumnKey();
    }

    @Test
    public void testSetGetKeyValues() {
        Map<String, Object> keyValues = new HashMap<String, Object>();
        keyValues.put("employee_id", 1);
        keyValues.put("employee_social_security_number", 820328193);
        key.setKeyValue(keyValues);
        assertEquals(keyValues, ((InverseJoinColumnKey) key).getKeyValues());
    }

    @Test
    public void testSetGetTableName() {
        String serializableTypeElementCollectionTableName = "phone_numbers";
        InverseJoinColumnKey inverseJoinColumnKey = ((InverseJoinColumnKey) key);
        inverseJoinColumnKey.setTableName(serializableTypeElementCollectionTableName);
        assertEquals(serializableTypeElementCollectionTableName, inverseJoinColumnKey.getTableName());
    }
}
