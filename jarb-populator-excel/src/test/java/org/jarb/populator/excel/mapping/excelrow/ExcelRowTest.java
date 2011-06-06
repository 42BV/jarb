package org.jarb.populator.excel.mapping.excelrow;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class ExcelRowTest extends DefaultExcelTestDataCase {

    private Workbook excel;
    private ExcelRow excelRow;
    private Customer createdInstance;
    private ClassDefinition classDefinition;
    private Class<?> persistentClass;
    private int retrievenKeyValue;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setUpExcelRecordTest() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, InvalidFormatException,
            IOException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
    }

    @Test
    public void testAddValueGetValueList() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException, SecurityException,
            NoSuchFieldException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
        excelRow = new ExcelRow(classDefinition.createInstance());

        PropertyDefinition columnDefinition = FieldAnalyzer.analyzeField(persistentClass.getDeclaredField("id"));

        Double cellValue = (Double) excel.getSheet(classDefinition.getTableName()).getCellValueAt(2, 0);
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
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
        excelRow = new ExcelRow(classDefinition.createInstance());

        createdInstance = (Customer) excelRow.getCreatedInstance();
        assertEquals(persistentClass, createdInstance.getClass());
    }

}
