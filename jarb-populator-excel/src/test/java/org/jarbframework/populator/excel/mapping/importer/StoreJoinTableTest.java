package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StoreJoinTableTest {
    private Class<?> persistentClass;
    private EntityDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Integer rowPosition;
    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

        context = new ClassPathXmlApplicationContext("test-context.xml");
        entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");

        //For code coverage purposes:
        Constructor<StoreJoinTable> constructor = StoreJoinTable.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testStoreJoinTableValue() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        persistentClass = domain.entities.Employee.class;

        classDefinition = EntityDefinition.forClass(persistentClass).setTableName("projects").build();

        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Employee.class);

        ClassDefinitionsGenerator classDefinitionsGenerator = new ClassDefinitionsGenerator(entityManagerFactory);
        classDefinition = classDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entity, false);

        excelRow = new ExcelRow(classDefinition.getEntityClass());

        FieldAnalyzer fieldAnalyzer = new FieldAnalyzer(JpaHibernateSchemaMapper.usingNamingStrategyOf(entityManagerFactory));
        PropertyDefinition joinTable = fieldAnalyzer.analyzeField(new PropertyReference(persistentClass, "projects")).build();

        rowPosition = 3;
        assertFalse(excelRow.getValueMap().containsKey(joinTable));
        new StoreExcelRecordValue(ValueConversionService.defaultConversions()).storeValue(excel, classDefinition, joinTable, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(joinTable));

    }

}
