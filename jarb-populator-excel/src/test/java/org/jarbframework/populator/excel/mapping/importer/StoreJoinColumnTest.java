package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.mapping.ValueConversionService;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarbframework.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarbframework.populator.excel.workbook.Workbook;
import org.jarbframework.populator.excel.workbook.reader.PoiWorkbookParser;
import org.jarbframework.utils.bean.PropertyReference;
import org.jarbframework.utils.orm.jpa.JpaHibernateSchemaMapper;
import org.junit.Before;
import org.junit.Test;

public class StoreJoinColumnTest extends DefaultExcelTestDataCase {

    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
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

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);

        excelRow = new ExcelRow(classDefinition.getEntityClass());

        rowPosition = 1;
        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(JpaHibernateSchemaMapper.usingNamingStrategyOf(getEntityManagerFactory()));
        PropertyDefinition joinColumn = fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "customer")).build();

        new StoreExcelRecordValue(new ValueConversionService()).storeValue(excel, classDefinition, joinColumn, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(joinColumn));
    }

    @Test
    public void testStoreValueNull() throws InstantiationException, ClassNotFoundException, IllegalAccessException, InvalidFormatException, IOException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ForeignKeyValueMissing.xls"));

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(getEntityManagerFactory());
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);
        excelRow = new ExcelRow(classDefinition.getEntityClass());

        StoreJoinColumn.storeValue(excel, classDefinition, classDefinition.property("customer"), 2, excelRow);
    }
}
