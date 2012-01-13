package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.EntityDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.bean.PropertyReference;
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
    }

    @Test
    public void testStoreNullClass() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(Employee.class);
        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        EntityDefinition<?> classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(entity, false);

        rowPosition = 3;

        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(getEntityManagerFactory());
        PropertyDefinition columnDefinition = fieldAnalyzer.analyzeField(new PropertyReference(Employee.class, "projects")).build();
        excelRow = new ExcelRow(Employee.class);
        new StoreExcelRecordValue(ValueConversionService.defaultConversions()).storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(columnDefinition));
    }

}
