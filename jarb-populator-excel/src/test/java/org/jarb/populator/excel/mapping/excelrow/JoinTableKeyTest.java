package org.jarb.populator.excel.mapping.excelrow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.junit.Before;
import org.junit.Test;

public class JoinTableKeyTest extends DefaultExcelTestDataCase {
    private Key key;
    private Set<Integer> keyValueSet;

    @Before
    public void setUpJoinTableKeyTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        key = new JoinTableKey();
        keyValueSet = new HashSet<Integer>();
        for (Integer i = 6; i <= 8; i++) {
            if (i != null) {
                keyValueSet.add(i);
            }
        }
    }

    @Test
    public void testSetGetKeyValues() {
        //Set the key value of the key object to the set that has just been filled
        key.setKeyValue(keyValueSet);

        //Retrieve the key values again and check if they match the ones in keyValueSet
        assertEquals(keyValueSet, ((JoinTableKey) key).getKeyValues());
    }

}
