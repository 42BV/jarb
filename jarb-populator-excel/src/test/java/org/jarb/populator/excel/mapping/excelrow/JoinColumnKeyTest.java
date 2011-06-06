package org.jarb.populator.excel.mapping.excelrow;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;

public class JoinColumnKeyTest extends DefaultExcelTestDataCase {
    private Key key;
    private ClassDefinition classDefinition;;
    private Integer testValue;
    private Workbook excel;
    private WorksheetDefinition worksheetDefinition;

    @Before
    public void setUpJoinColumnKey() throws InstantiationException, IllegalAccessException, InvalidFormatException, IOException, ClassNotFoundException {
        key = new JoinColumnKey();
        testValue = new Integer(1);
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
    }

    @Test
    public void testSetGetKeyValue() {
        key.setKeyValue(testValue);
        assertEquals(testValue, ((JoinColumnKey) key).getKeyValue());
    }

    @Test
    public void testSetGetForeignClass() {
        key.setForeignClass(Customer.class);
        assertEquals(Customer.class, key.getForeignClass());
    }
}