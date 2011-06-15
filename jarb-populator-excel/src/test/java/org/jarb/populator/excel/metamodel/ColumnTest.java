package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class ColumnTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private ClassDefinition<?> classDefinition;
    private ColumnDefinition column = new Column("name");
    private Workbook excel;
    private Field nameField;
    private String columnName;
    private String fieldName = "name";
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setupColumnTest() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, SecurityException,
            NoSuchFieldException, ClassNotFoundException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");

        column = FieldAnalyzer.analyzeField(nameField);

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.addPropertyDefinition(column);

        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);
    }

    @Test
    public void testIsAccociativeTable() {
        for (ColumnDefinition columnDefinition : classDefinition.getColumnDefinitions()) {
            if (columnDefinition.getColumnName().equals("employees_projects")) {
                assertEquals(true, columnDefinition.isAssociativeTable());
            } else {
                assertEquals(false, columnDefinition.isAssociativeTable());
            }
        }
    }

    @Test
    public void testStoreAnnotation() throws SecurityException, NoSuchFieldException {

        for (Annotation annotation : nameField.getAnnotations()) {
            column.storeAnnotation(nameField, annotation);
        }
        assertEquals("first_name", column.getColumnName());
    }

    @Test
    public void testSetGetField() throws SecurityException, NoSuchFieldException {
        column.setField(nameField);
        assertEquals(nameField, column.getField());
    }

    @Test
    public void testSetGetColumnName() {
        columnName = "first_name";
        column.setColumnName(columnName);
        assertEquals(columnName, column.getColumnName());
    }

    @Test
    public void testSetGetFieldName() {
        assertEquals(fieldName, column.getFieldName());
    }

    @Test
    public void testSetIsEmbeddedAttribute() {
        column.setEmbeddedAttribute(true);
        assertEquals(true, column.isEmbeddedAttribute());
    }

    @Test
    public void testSetGetEmbeddedObjectName() {
        column.setEmbeddedObjectName("address");
        assertEquals("address", column.getEmbeddedObjectName());
    }
}
