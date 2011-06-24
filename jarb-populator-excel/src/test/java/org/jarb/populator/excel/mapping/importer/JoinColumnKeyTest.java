package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.importer.JoinColumnKey;
import org.jarb.populator.excel.mapping.importer.Key;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class JoinColumnKeyTest extends DefaultExcelTestDataCase {
    private Key key;
    private Integer testValue;

    @Before
    public void setUpJoinColumnKey() throws InstantiationException, IllegalAccessException, InvalidFormatException, IOException, ClassNotFoundException {
        key = new JoinColumnKey();
        testValue = new Integer(1);
    }

    @Test
    public void testSetGetKeyValue() {
        key.setKeyValue(testValue);
        assertEquals(testValue, ((JoinColumnKey) key).getKeyValue());
    }

    @Test
    public void testSetGetForeignClass() {
        key.setForeignClass(Customer.class);
        assertEquals(Customer.class, key.getForeignClass());
    }
}