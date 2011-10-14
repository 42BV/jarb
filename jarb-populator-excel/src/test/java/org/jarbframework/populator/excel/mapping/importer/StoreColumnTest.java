package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.bean.ModifiableBean;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

public class StoreColumnTest extends DefaultExcelTestDataCase {
    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Field nameField;
    private WorksheetDefinition worksheetDefinition;
    private Integer rowPosition;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        //For code coverage purposes:
        Constructor<StoreColumn> constructor = StoreColumn.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testStoreColumnValue() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;
        nameField = persistentClass.getDeclaredField("name");

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
        PropertyDefinition column = fieldAnalyzer.analyzeField(nameField, persistentClass).build();
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);

        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);
        excelRow = new ExcelRow(classDefinition.getEntityClass());

        rowPosition = 1;
        StoreExcelRecordValue.storeValue(excel, classDefinition, column, rowPosition, excelRow);
        assertEquals("Customer1", ModifiableBean.wrap(excelRow.getCreatedInstance()).getPropertyValue("name"));
    }

}
