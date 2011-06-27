package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.importer.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class FieldAnalyzerTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private Field nameField;
    private Field idField;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setupAnalyzeField() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");
        idField = persistentClass.getDeclaredField("id");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);

        //For code coverage purposes:
        Constructor<FieldAnalyzer> constructor = FieldAnalyzer.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testColumn() {    
        PropertyDefinition testNameColumn = FieldAnalyzer.analyzeField(nameField).build();
        assertEquals("first_name", testNameColumn.getColumnName());
        assertEquals("name", testNameColumn.getName());
    }
    

    @Test
    public void testJoinTable() throws SecurityException, NoSuchFieldException {
        PropertyDefinition definition = FieldAnalyzer.analyzeField(Employee.class.getDeclaredField("projects")).build();
        assertEquals("project_id", definition.getInverseJoinColumnName());
        assertEquals("employee_id", definition.getJoinColumnName());
    }
    
    @Test
    public void testGeneratedValue() {
        assertTrue(FieldAnalyzer.analyzeField(idField).build().isGeneratedValue());
    }

    @Test
    public void testAnalyzeFieldNull() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        assertEquals(null, FieldAnalyzer.analyzeField(domain.entities.Document.class.getDeclaredField("documentRevisions")));
    }
    

}
