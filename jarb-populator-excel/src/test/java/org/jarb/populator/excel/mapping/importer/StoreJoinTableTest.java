package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.mapping.excelrow.ExcelRow;
import org.jarb.populator.excel.metamodel.AnnotationType;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.metamodel.generator.FieldAnalyzer;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StoreJoinTableTest {
    private Class<?> persistentClass;
    private ClassDefinition<?> classDefinition;
    private Workbook excel;
    private ExcelRow excelRow;
    private Field projectsField;
    private Integer rowPosition;
    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setupTestStoreExcelRecordValue() throws InvalidFormatException, IOException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/ExcelUnitTesting.xls"));

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

        projectsField = persistentClass.getDeclaredField("projects");

        classDefinition = ClassDefinition.forClass(persistentClass);

        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Employee.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, false);

        excelRow = new ExcelRow(classDefinition.createInstance());

        ColumnDefinition joinTable;
        for (Annotation annotation : projectsField.getAnnotations()) {
            for (AnnotationType annotationType : AnnotationType.values()) {
                if ((annotationType.name().equals("JOIN_TABLE")) && annotationType.getAnnotationClass().isAssignableFrom(annotation.getClass())) {
                    joinTable = annotationType.createColumnDefinition("projects");
                    joinTable.storeAnnotation(projectsField, annotation);
                }
            }
        }
        joinTable = FieldAnalyzer.analyzeField(projectsField);
        joinTable.setField(projectsField);
        joinTable.setColumnName("TestSheet4");
        rowPosition = 3;
        assertFalse(excelRow.getValueMap().containsKey(joinTable));
        StoreExcelRecordValue.storeValue(excel, classDefinition, joinTable, rowPosition, excelRow);
        assertTrue(excelRow.getValueMap().containsKey(joinTable));

    }

}
