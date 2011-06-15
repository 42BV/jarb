package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class FieldAnalyzerTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private ClassDefinition<?> classDefinition;
    private ColumnDefinition testNameColumn = new Column("name");
    private Workbook excel;
    private Field nameField;
    private Field idField;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setupAnalyzeField() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");
        idField = persistentClass.getDeclaredField("id");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

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
    public void testAnalyzeField() throws InstantiationException, IllegalAccessException {
        //Test a field where javax.persistence.GeneratedValue.class is not null
        assertEquals(null, FieldAnalyzer.analyzeField(idField));

        //Test a field which has annotations but where javax.persistence.GeneratedValue.class is null        
        testNameColumn.setField(nameField);
        testNameColumn.setColumnName("first_name");
        assertEquals(testNameColumn.getColumnName(), FieldAnalyzer.analyzeField(nameField).getColumnName());
        assertEquals(testNameColumn.getFieldName(), FieldAnalyzer.analyzeField(nameField).getFieldName());
    }

    @Test
    public void testAnalyzeFieldNull() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        assertEquals(null, FieldAnalyzer.analyzeField(domain.entities.Document.class.getDeclaredField("documentRevisions")));
    }

}
