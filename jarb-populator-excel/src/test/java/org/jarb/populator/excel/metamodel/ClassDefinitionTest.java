package org.jarb.populator.excel.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import domain.entities.Customer;

public class ClassDefinitionTest {

    private ClassDefinition<?> classDefinition;
    private ColumnDefinition columnDefinition;
    private List<ColumnDefinition> columnDefinitions;
    private String tableName;
    private WorksheetDefinition worksheetDefinition;
    private Workbook excel;
    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory entityManagerFactory;
    private Metamodel metamodel;
    private EntityType<?> entity;

    @Before
    public void setUpClassDefinition() throws InvalidFormatException, IOException {
        classDefinition = ClassDefinition.forClass(Customer.class);
        columnDefinitions = new ArrayList<ColumnDefinition>();
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        context = new ClassPathXmlApplicationContext("test-context.xml");
        entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");

        metamodel = entityManagerFactory.getMetamodel();
        entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);
    }

    @Test
    public void testNewInstance() throws InstantiationException, IllegalAccessException {
        assertTrue(classDefinition.createInstance() instanceof Customer);
    }

    @Test
    public void testGetPersistentClass() {
        assertEquals(Customer.class, classDefinition.getPersistentClass());
    }

    @Test
    public void testAddGetColumnDefinitions() throws InstantiationException, IllegalAccessException {
        for (Field field : classDefinition.getPersistentClass().getDeclaredFields()) {
            columnDefinition = FieldAnalyzer.analyzeField(field);
            if (columnDefinition != null) {
                classDefinition.addPropertyDefinition(columnDefinition);
                columnDefinitions.add(columnDefinition);
            }
        }
        assertEquals(classDefinition.getPropertyDefinitions(), columnDefinitions);
    }

    @Test
    public void testSetGetTableName() {
        tableName = "customers";
        classDefinition.setTableName(tableName);
        assertEquals(tableName, classDefinition.getTableName());
    }

    @Test
    public void testSetGetWorksheetDefinition() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));
        worksheetDefinition = classDefinition.getWorksheetDefinition();
        Integer columnPosition = 2;
        assertEquals(columnPosition, worksheetDefinition.getColumnPosition("company_name"));
    }

    @Test
    public void testgetDiscriminatorColumn() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, true);
        assertEquals("type", classDefinition.getDiscriminatorColumnName());
    }

    @Test
    public void testGetColumnDefinitionByColumnName() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, false);
        assertEquals("company_name", classDefinition.getPropertyDefinitionByColumnName("company_name").getColumnName());
    }

    @Test
    public void testToString() {
        assertEquals("domain.entities.Customer", classDefinition.toString());
    }
}
