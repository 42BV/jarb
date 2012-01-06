package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.persistence.Column;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.junit.Test;

public class ColumnMetadataRetrieverTest extends DefaultExcelTestDataCase {

    @Test
    public void testGetColumnNamesForClass() {
        Set<String> columnnames = ColumnMetadataRetriever.getColumnNamesForClass(Person.class);
        assertEquals(4, columnnames.size());
        assertTrue("Columnnames should be retrieved from Column annotation if available", columnnames.contains("idcolumn"));
        assertTrue("Columnnames should be generated if no Column annotation is available", columnnames.contains("name"));
        assertTrue(columnnames.contains("salary"));
        assertTrue("Columnnames should be generated if the Column annotation has no specified name", columnnames.contains("badlyAnnotatedField"));
    }

}

class Person {
    @Column(name = "idcolumn")
    private int id;

    private String name;

    @Column(name = "salary")
    private int salary;

    @Column
    private String badlyAnnotatedField;
}
