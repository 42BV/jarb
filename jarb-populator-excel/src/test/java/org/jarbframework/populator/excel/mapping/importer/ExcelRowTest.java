package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class ExcelRowTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private ExcelRow excelRow;
    private Customer createdInstance;
    private EntityDefinition<?> classDefinition;
    private Class<?> persistentClass;
    private Object retrievenKeyValue;

    @Before
    public void setUpExcelRecordTest() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException {
        excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
    }

    @Test
    public void testAddValueGetValueList() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, SecurityException,
            NoSuchFieldException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);
        excelRow = new ExcelRow(classDefinition.getEntityClass());

        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
        PropertyDefinition columnDefinition = fieldAnalyzer.analyzeField(persistentClass.getDeclaredField("id"), persistentClass).build();

        Double cellValue = (Double) excel.getSheet(classDefinition.getTableName()).getValueAt(2, 0);
        Key keyValue = new JoinColumnKey();
        keyValue.setKeyValue(cellValue.intValue());
        excelRow.addValue(columnDefinition, keyValue);
        retrievenKeyValue = ((JoinColumnKey) excelRow.getValueMap().get(columnDefinition)).getKeyValue();
        assertEquals(3, retrievenKeyValue);
    }

    @Test
    public void testGetCreatedInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);
        excelRow = new ExcelRow(classDefinition.getEntityClass());

        createdInstance = (Customer) excelRow.getCreatedInstance();
        assertEquals(persistentClass, createdInstance.getClass());
    }

}
