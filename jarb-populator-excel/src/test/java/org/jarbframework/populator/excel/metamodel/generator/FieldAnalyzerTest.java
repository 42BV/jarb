package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.importer.WorksheetDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Document;
import domain.entities.Employee;

public class FieldAnalyzerTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private Field nameField;
    private Field idField;
    private WorksheetDefinition worksheetDefinition;
    private FieldAnalyzer fieldAnalyzer;

    @Before
    public void setupAnalyzeField() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");
        idField = persistentClass.getDeclaredField("id");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);
        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);

        fieldAnalyzer = new FieldAnalyzer(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
    }

    @Test
    public void testColumn() {
        PropertyDefinition testNameColumn = fieldAnalyzer.analyzeField(nameField, persistentClass).build();
        assertEquals("first_name", testNameColumn.getColumnName());
        assertEquals("name", testNameColumn.getName());
    }

    @Test
    public void testJoinTable() throws SecurityException, NoSuchFieldException {
        PropertyDefinition definition = fieldAnalyzer.analyzeField(Employee.class.getDeclaredField("projects"), Employee.class).build();
        assertEquals("project_id", definition.getInverseJoinColumnName());
        assertEquals("employee_id", definition.getJoinColumnName());
    }

    @Test
    public void testGeneratedValue() {
        assertTrue(fieldAnalyzer.analyzeField(idField, persistentClass).build().isGeneratedValue());
    }

    @Test
    public void testAnalyzeFieldNull() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        assertNull(fieldAnalyzer.analyzeField(Document.class.getDeclaredField("documentRevisions"), Document.class));
    }

}
