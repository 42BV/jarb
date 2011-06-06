package org.jarb.populator.excel.mapping.importer;

import static org.junit.Assert.assertFalse;

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
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StoreExcelRecordValueTest {

    private Class<?> persistentClass;
    private ClassDefinition classDefinition;
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
        Constructor<StoreExcelRecordValue> constructor = StoreExcelRecordValue.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testStoreNullClass() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        persistentClass = domain.entities.Employee.class;

        projectsField = persistentClass.getDeclaredField("projects");

        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Employee.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(entityManagerFactory, entity, false);
        classDefinition.setWorksheetDefinition(WorksheetDefinition.analyzeWorksheet(classDefinition, excel));

        excelRow = new ExcelRow(classDefinition.createInstance());

        PropertyDefinition joinTable;
        for (Annotation annotation : projectsField.getAnnotations()) {
            for (AnnotationType annotationType : AnnotationType.values()) {
                if ((annotationType.name().equals("JOIN_TABLE")) && annotationType.getAnnotationClass().isAssignableFrom(annotation.getClass())) {
                    joinTable = annotationType.createColumnDefinition("projects");
                    joinTable.storeAnnotation(projectsField, annotation);
                }
            }
        }
        rowPosition = 3;
        PropertyDefinition columnDefinition = null;
        StoreExcelRecordValue.storeValue(excel, classDefinition, columnDefinition, rowPosition, excelRow);
        assertFalse(excelRow.getValueMap().containsKey(columnDefinition));
    }

}
