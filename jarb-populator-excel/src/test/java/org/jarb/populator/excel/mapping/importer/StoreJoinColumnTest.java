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
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

public class StoreJoinColumnTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Field customerField;
    private Integer rowPosition;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

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
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        excelRow = new ExcelRow(classDefinition.getEntityClass());

        rowPosition = 1;
        PropertyDefinition joinColumn = FieldAnalyzer.analyzeField(customerField).build();

        StoreExcelRecordValue.storeValue(excel, classDefinition, joinColumn, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(joinColumn));
    }

    @Test
    public void testStoreValueNull() throws InstantiationException, ClassNotFoundException, IllegalAccessException, InvalidFormatException, IOException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ForeignKeyValueMissing.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        excelRow = new ExcelRow(classDefinition.getEntityClass());

        StoreJoinColumn.storeValue(excel, classDefinition, classDefinition.property("customer"), 2, excelRow);
    }
}
