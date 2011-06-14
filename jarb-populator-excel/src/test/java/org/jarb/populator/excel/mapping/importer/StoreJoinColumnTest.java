package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.JoinColumn;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;

public class StoreJoinColumnTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private ClassDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Field customerField;
    private Integer rowPosition;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<StoreJoinColumn> constructor = StoreJoinColumn.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testStoreJoinColumnValue() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        persistentClass = domain.entities.Project.class;
        customerField = persistentClass.getDeclaredField("customer");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));

        excelRow = new ExcelRow(classDefinition.createInstance());

        rowPosition = 1;
        JoinColumn joinColumn = (JoinColumn) FieldAnalyzer.analyzeField(customerField);
        joinColumn.setField(customerField);
        joinColumn.setColumnName("customer");

        StoreExcelRecordValue.storeValue(excel, classDefinition, joinColumn, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(joinColumn));
    }

    @Test
    public void testStoreValueNull() throws InstantiationException, ClassNotFoundException, IllegalAccessException, InvalidFormatException, IOException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ForeignKeyValueMissing.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));
        excelRow = new ExcelRow(classDefinition.createInstance());

        StoreJoinColumn.storeValue(excel, classDefinition, classDefinition.getColumnDefinitionByFieldName("customer"), 2, excelRow);
    }
}
