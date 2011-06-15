package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class ClassDefinitionTest extends DefaultExcelTestDataCase {
    private ClassDefinition.Builder<Customer> classDefinitionBuilder;
    private ClassDefinition<Customer> customerDefinition;

    @Before
    public void setUpClassDefinitions() throws InvalidFormatException, IOException {
        // Retrieve full customer definition from meta model generator
        customerDefinition = generateMetamodel().findClassDefinition(Customer.class);
        // Build a customer class definition
        classDefinitionBuilder = ClassDefinition.forClass(Customer.class).setTableName("customers");
    }

    @Test
    public void testGetPersistentClass() {
        assertEquals(Customer.class, classDefinitionBuilder.build().getPersistentClass());
    }

    @Test
    public void testAddGetColumnDefinitions() throws InstantiationException, IllegalAccessException {
        List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
        for (Field field : Customer.class.getDeclaredFields()) {
            columnDefinitions.add(FieldAnalyzer.analyzeField(field).build());
        }
        classDefinitionBuilder.includeColumns(columnDefinitions);
        List<ColumnDefinition> resultColumnDefinitions = classDefinitionBuilder.build().getColumnDefinitions();
        for(ColumnDefinition columnDefinition : columnDefinitions) {
            assertTrue(resultColumnDefinitions.contains(columnDefinition));
        }
    }

    @Test
    public void testSetGetTableName() {
        final String tableName = "someTable";
        classDefinitionBuilder.setTableName(tableName);
        assertEquals(tableName, classDefinitionBuilder.build().getTableName());
    }
    
    @Test
    public void testToString() {
        assertEquals("domain.entities.Customer", classDefinitionBuilder.build().toString());
    }

    @Test
    public void testgetDiscriminatorColumn() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        assertEquals("type", customerDefinition.getDiscriminatorColumnName());
    }

    @Test
    public void testGetColumnDefinitionByColumnName() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        assertEquals("company_name", customerDefinition.getColumnDefinitionByColumnName("company_name").getColumnName());
    }

}
