package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.EntityDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.bean.FlexibleBeanWrapper;
import org.jarbframework.utils.bean.PropertyReference;
import org.junit.Before;
import org.junit.Test;

public class StoreColumnTest extends DefaultExcelTestDataCase {
    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private WorksheetDefinition worksheetDefinition;
    private Integer rowPosition;

    @Before
    public void setupTestStoreExcelRecordValue() throws FileNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));
    }

    @Test
    public void testStoreColumnValue() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        persistentClass = domain.entities.Customer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(getEntityManagerFactory());
        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(getEntityManagerFactory());
        PropertyDefinition column = fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "name")).build();
        classDefinition = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(entity, false);

        worksheetDefinition = new WorksheetDefinition();
        worksheetDefinition = WorksheetDefinition.analyzeWorksheet(classDefinition, excel);
        worksheetDefinition.addColumnPosition("address", "customers", 0);
        excelRow = new ExcelRow(classDefinition.getDefinedClass());

        rowPosition = 1;
        new StoreExcelRecordValue(ValueConversionService.defaultConversions()).storeValue(excel, classDefinition, column, rowPosition, excelRow);
        assertEquals("Customer1", FlexibleBeanWrapper.wrap(excelRow.getCreatedInstance()).getPropertyValue("name"));
    }
}
