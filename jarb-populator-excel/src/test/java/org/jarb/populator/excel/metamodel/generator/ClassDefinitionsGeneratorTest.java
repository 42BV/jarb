package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.util.ClassDefinitionNameComparator;
import org.jarb.populator.excel.workbook.Workbook;
import org.jarb.populator.excel.workbook.reader.PoiExcelParser;
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
    public void testclassDefinitionsGeneration() throws UnsupportedEncodingException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            SecurityException, NoSuchFieldException {
        List<ClassDefinition<?>> classDefinitionsGenerated = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());
        Collections.sort(classDefinitionsGenerated, new ClassDefinitionNameComparator());

        List<String> classDefinitionsManual = new ArrayList<String>();
        classDefinitionsManual.add("NoTableAnnotation");
        classDefinitionsManual.add("customers");
        classDefinitionsManual.add("departments");
        classDefinitionsManual.add("employees");
        classDefinitionsManual.add("employees_projects_workspaces");
        classDefinitionsManual.add("projects");
        classDefinitionsManual.add("workspaces");

        for (int i = 0; i <= 2; i++) {
            assertEquals(classDefinitionsGenerated.get(i).getTableName(), classDefinitionsManual.get(i));
        }
    }

    @Test
    public void testCreateSingleClassDefinition() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Department.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Department.class, metamodel);

        ClassDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        ColumnDefinition generated = classDefinition.getPropertyDefinitionByFieldName("departmentName");
        ColumnDefinition departmentName = new Column("departmentName");
        departmentName.setColumnName("department_name");
        departmentName.setField(persistentClass.getDeclaredField("departmentName"));
        assertEquals(generated.getField(), departmentName.getField());
    }

    @Test
    public void testCreateSingleClassDefinitionWithSubclass() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<?> subClass = domain.entities.SpecialCustomer.class;

        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Customer.class, metamodel);

        ClassDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, true);
        ColumnDefinition generated = classDefinition.getPropertyDefinitionByFieldName("location");
        ColumnDefinition companyLocation = new Column("location");
        companyLocation.setColumnName("company_location");
        companyLocation.setField(subClass.getDeclaredField("location"));
        assertEquals(generated.getField(), companyLocation.getField());
    }

    @Test
    public void testCreateSingleClassDefinitionNull() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.UnannotatedClass.class, metamodel);

        ClassDefinition<?> classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);
        assertEquals(null, classDefinition);
    }

    @Test
    public void testCreateClassDefinitionsFromMetamodel() throws ClassNotFoundException, SecurityException, InstantiationException, IllegalAccessException,
            NoSuchFieldException {
        List<ClassDefinition<?>> classDefinitionList = ClassDefinitionsGenerator.createClassDefinitionsFromMetamodel(getEntityManagerFactory());

        for (ClassDefinition<?> classDefinition : classDefinitionList) {
            //Check if it holds a persistent class
            Class<?> persistentClass = classDefinition.getPersistentClass();
            assertTrue(persistentClass != null);

            //Should not contain a WorksheetDefinition
            assertTrue(classDefinition.getWorksheetDefinition() == null);

            for (ColumnDefinition columnDefinition : classDefinition.getPropertyDefinitions()) {
                if (columnDefinition.getField() != null) {
                    //Is a regular column, we should be able to get the data from the persistentclass

                    Field field = null;

                    try {
                        field = persistentClass.getDeclaredField(columnDefinition.getFieldName());
                    } catch (NoSuchFieldException e) {
                        //Field is not in persistentclass, checking subclasses.
                    }
                    if (field == null) {
                        //Might be from a subclass, try to reach it from there.
                        for (Class<?> _class : classDefinition.getSubClasses().values()) {
                            if (field == null) {
                                try {
                                    field = _class.getDeclaredField(columnDefinition.getFieldName());
                                } catch (NoSuchFieldException e) {
                                }
                            }
                        }
                    }
                    if ((field == null) && columnDefinition.isEmbeddedAttribute()) {
                        //It's an embedded attribute.
                        Field embeddedField = columnDefinition.getField();
                        field = embeddedField.getDeclaringClass().getDeclaredField(columnDefinition.getFieldName());
                    }

                    //Check if field is valid
                    if (field != null) {
                        assertEquals(field.getName(), columnDefinition.getFieldName());
                        assertFalse(columnDefinition.getColumnName() == null);
                    }
                } else {
                    //Should be a discriminatorColumn.
                    assertTrue(columnDefinition.isDiscriminatorColumn());
                }
            }

        }

    }

    public void testAddWorksheetDefinitionsToClassDefinitions() throws InvalidFormatException, IOException, InstantiationException, ClassNotFoundException,
            IllegalAccessException {
        Workbook excel = new PoiExcelParser().parse(new FileInputStream("src/test/resources/Excel.xls"));

        List<ClassDefinition<?>> classDefinitions = new ArrayList<ClassDefinition<?>>();
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Department.class, metamodel);

        classDefinitions.add(ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false));
        ClassDefinitionsGenerator.addWorksheetDefinitionsToClassDefinitions(classDefinitions, excel);
        ClassDefinition<?> classDefinition = classDefinitions.get(0);
        Integer columnPosition = 1;
        assertEquals(columnPosition, classDefinition.getWorksheetDefinition().getColumnPosition("db_column"));
    }

}
