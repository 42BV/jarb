package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.jarb.populator.excel.metamodel.EntityDefinition;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.Department;
import domain.entities.Employee;

public class ColumnDefinitionsGeneratorTest extends DefaultExcelTestDataCase {
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setupColumnDefinitionsGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        entityManagerFactory = getEntityManagerFactory();

        //For code coverage purposes:
        Constructor<ColumnDefinitionsGenerator> constructor = ColumnDefinitionsGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateColumnDefinitions() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<Department> persistentClass = Department.class;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        Field departmentNameField = persistentClass.getDeclaredField("departmentName");
        EntityDefinition.Builder<Department> classDefinitionBuilder = EntityDefinition.forClass(Department.class).setTableName("departments");
        classDefinitionBuilder.includeProperties(ColumnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(departmentNameField, classDefinitionBuilder.build().property("departmentName").getField());
    }

    @Test
    public void testCreateColumnDefinitionsWithEmbeddables() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<Employee> persistentClass = Employee.class;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        Field buildingAddressField = Address.class.getDeclaredField("streetAndNumber");
        EntityDefinition.Builder<Employee> classDefinitionBuilder = EntityDefinition.forClass(Employee.class).setTableName("employees");
        classDefinitionBuilder.includeProperties(ColumnDefinitionsGenerator.createPropertyDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(buildingAddressField, classDefinitionBuilder.build().property("streetAndNumber").getField());
    }
}
