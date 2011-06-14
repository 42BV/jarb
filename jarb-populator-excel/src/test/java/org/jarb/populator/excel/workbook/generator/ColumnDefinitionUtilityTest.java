package org.jarb.populator.excel.workbook.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.jarb.populator.excel.metamodel.generator.ClassDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;

public class ColumnDefinitionUtilityTest extends DefaultExcelTestDataCase {
    private ClassDefinition<?> classDefinition;

    @Before
    public void setupColumnDefinitionUtilityTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Metamodel metamodel = getEntityManagerFactory().getMetamodel();
        EntityType<?> entity = ClassDefinitionsGenerator.getEntityFromMetamodel(domain.entities.Employee.class, metamodel);

        classDefinition = ClassDefinitionsGenerator.createSingleClassDefinitionFromMetamodel(getEntityManagerFactory(), entity, false);

        //For code coverage purposes:
        Constructor<ColumnDefinitionUtility> constructor = ColumnDefinitionUtility.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGatherAssociativeColumnDefinitions() {
        Set<PropertyDefinition> manual = new HashSet<PropertyDefinition>();
        manual.add(classDefinition.getColumnDefinitionByFieldName("projects"));
        assertEquals(manual, ColumnDefinitionUtility.gatherAssociativeColumnDefinitions(classDefinition));
    }

    @Test
    public void testGetFieldName() {
        assertEquals("salary", ColumnDefinitionUtility.getFieldName(classDefinition, "salary_month"));
    }

    @Test
    public void testGetFieldNameId() {
        assertEquals("id", ColumnDefinitionUtility.getFieldName(classDefinition, "id"));
    }

}
