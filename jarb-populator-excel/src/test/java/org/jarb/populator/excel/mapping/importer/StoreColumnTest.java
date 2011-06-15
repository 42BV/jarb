package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

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
import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.jarb.utils.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

public class StoreColumnTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private ClassDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Field nameField;
    private WorksheetDefinition worksheetDefinition;
    private Integer rowPosition;
    private ExcelRow excelRecord2;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<StoreColumn> constructor = StoreColumn.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testWrongInstanceColumns() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        persistentClass = domain.entities.Project.class;
        nameField = persistentClass.getDeclaredField("name");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Project.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));

        excelRow = new ExcelRow(classDefinition.createInstance());
        excelRecord2 = new ExcelRow(classDefinition.createInstance());

        rowPosition = 1;
        Column column = (Column) FieldAnalyzer.analyzeField(nameField);
        column.setField(nameField);
        column.setColumnName("name");

        //StoreExcelRecordValue.storeValue(excel, classDefinition, column, rowPosition, excelRow);
        StoreJoinColumn.storeValue(excel, classDefinition, column, rowPosition, excelRow);
        StoreJoinTable.storeValue(excel, classDefinition, column, rowPosition, excelRecord2);
    }

    @Test
    public void testStoreColumnValue() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        ColumnDefinition column = FieldAnalyzer.analyzeField(nameField);
        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        classDefinition.addPropertyDefinition(column);

        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);
        classDefinition.setWorksheetDefinition(worksheetDefinition);
        excelRow = new ExcelRow(classDefinition.createInstance());

        rowPosition = 1;
        StoreExcelRecordValue.storeValue(excel, classDefinition, column, rowPosition, excelRow);
        assertEquals("Customer1", ReflectionUtils.getFieldValue(excelRow.getCreatedInstance(), "name"));
    }

}
