package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.importer.WorksheetDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Document;
import domain.entities.Employee;

public class FieldAnalyzerTest extends DefaultExcelTestDataCase {
    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private WorksheetDefinition worksheetDefinition;
    private FieldAnalyzer fieldAnalyzer;

    @Before
    public void setupAnalyzeField() throws FileNotFoundException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
        persistentClass = domain.entities.Customer.class;

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
        PropertyDefinition testNameColumn = fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "name")).build();
        assertEquals("first_name", testNameColumn.getColumnName());
        assertEquals("name", testNameColumn.getName());
    }

    @Test
    public void testJoinTable() throws SecurityException, NoSuchFieldException {
        PropertyDefinition definition = fieldAnalyzer.analyzeField(new PropertyReference(Employee.class, "projects")).build();
        assertEquals("project_id", definition.getInverseJoinColumnName());
        assertEquals("employee_id", definition.getJoinColumnName());
    }

    @Test
    public void testGeneratedValue() {
        assertTrue(fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "id")).build().isGeneratedValue());
    }

    @Test
    public void testAnalyzeFieldNull() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
        assertNull(fieldAnalyzer.analyzeField(new PropertyReference(Document.class, "documentRevisions")));
    }

}
