package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.mapping.importer.WorksheetDefinition;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiWorkbookParser;
import org.junit.Before;
import org.junit.Test;

public class ClassDefinitionsGeneratorTest extends DefaultExcelTestDataCase {

    @Before
    public void setupClassDefinitionsGeneratorTest() throws InvalidFormatException, IOException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {

        //For code coverage purposes:
        Constructor<ClassDefinitionsGenerator> constructor = ClassDefinitionsGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateSingleClassDefinition() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Department.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Department.class);

        EntityDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        PropertyDefinition generated = classDefinition.property("departmentName");
        Field departmentNameField = persistentClass.getDeclaredField("departmentName");
        assertEquals(departmentNameField, generated.getField());
    }

    @Test
    public void testCreateSingleClassDefinitionWithSubclass() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<?> subClass = domain.entities.SpecialCustomer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Customer.class);

        EntityDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, true);
        PropertyDefinition generated = classDefinition.property("location");
        Field companyLocationField = subClass.getDeclaredField("location");
        assertEquals(companyLocationField, generated.getField());
    }

    public void testAddWorksheetDefinitionsToClassDefinitions() throws InvalidFormatException, IOException, InstantiationException, ClassNotFoundException,
            IllegalAccessException {
        Workbook excel = new PoiWorkbookParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        List<EntityDefinition<?>> classDefinitions = new ArrayList<EntityDefinition<?>>();
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = metamodel.entity(domain.entities.Department.class);

        classDefinitions.add(ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false));
        EntityDefinition<?> classDefinition = classDefinitions.get(0);
        Integer columnPosition = 1;
        assertEquals(columnPosition, WorksheetDefinition.analyzeWorksheet(classDefinition, excel).getColumnPosition("db_column"));
    }

}
