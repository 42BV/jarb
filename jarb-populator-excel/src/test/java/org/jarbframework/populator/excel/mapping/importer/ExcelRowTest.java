package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.EntityDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.utils.bean.PropertyReference;
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
    public void setUpExcelRecordTest() throws FileNotFoundException {
        excel = getExcelDataManagerFactory().buildExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
    }

    @Test
    public void testAddValueGetValueList() {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(entity, false);
        excelRow = new ExcelRow(classDefinition.getDefinedClass());

        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(getEntityManagerFactory());
        PropertyDefinition columnDefinition = fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "id")).build();

        Double cellValue = (Double) excel.getSheet(classDefinition.getTableName()).getValueAt(2, 0);
        Key keyValue = new JoinColumnKey();
        keyValue.setKeyValue(cellValue.intValue());
        excelRow.addValue(columnDefinition, keyValue);
        retrievenKeyValue = ((JoinColumnKey) excelRow.getValueMap().get(columnDefinition)).getKeyValue();
        assertEquals(3, retrievenKeyValue);
    }

    @Test
    public void testGetCreatedInstance() {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(entity, false);
        excelRow = new ExcelRow(classDefinition.getDefinedClass());

        createdInstance = (Customer) excelRow.getCreatedInstance();
        assertEquals(persistentClass, createdInstance.getClass());
    }

}
