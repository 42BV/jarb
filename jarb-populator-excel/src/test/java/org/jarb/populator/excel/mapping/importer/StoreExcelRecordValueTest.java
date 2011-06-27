package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class StoreExcelRecordValueTest extends DefaultExcelTestDataCase {
    private Workbook excel;
    private ExcelRow excelRow;
    private Integer rowPosition;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<StoreExcelRecordValue> constructor = StoreExcelRecordValue.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testStoreNullClass() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, ClassNotFoundException {       
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(Employee.class);
        EntityDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        
        rowPosition = 3;
        PropertyDefinition columnDefinition = FieldAnalyzer.analyzeField(Employee.class.getDeclaredField("projects")).build();
        excelRow = new ExcelRow(Employee.class);
        StoreExcelRecordValue.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(columnDefinition));
    }

}
