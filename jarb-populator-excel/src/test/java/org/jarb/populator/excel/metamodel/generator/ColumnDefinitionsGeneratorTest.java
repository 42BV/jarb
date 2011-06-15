package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarb.populator.excel.metamodel.ClassDefinition;
import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import domain.entities.Department;
import domain.entities.Employee;

public class ColumnDefinitionsGeneratorTest {

    private EntityManagerFactory entityManagerFactory;
    private ClassPathXmlApplicationContext context;

    @Before
    public void setupColumnDefinitionsGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        context = new ClassPathXmlApplicationContext("test-context.xml");
        entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");

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
        ColumnDefinition departmentName = new Column("departmentName");
        departmentName.setColumnName("department_name");
        departmentName.setField(persistentClass.getDeclaredField("departmentName"));
        ClassDefinition<Department> classDefinition = new ClassDefinition<Department>(persistentClass);
        classDefinition.addPropertyDefinitionList(ColumnDefinitionsGenerator.createColumnDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(departmentName.getField(), classDefinition.getPropertyDefinitionByFieldName("departmentName").getField());
    }

    @Test
    public void testCreateColumnDefinitionsWithEmbeddables() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<Employee> persistentClass = Employee.class;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        ColumnDefinition buildingAddress = new Column("streetAndNumber");
        buildingAddress.setColumnName("streetAndNumber");
        buildingAddress.setField(domain.entities.Address.class.getDeclaredField("streetAndNumber"));
        ClassDefinition<Employee> classDefinition = new ClassDefinition<Employee>(persistentClass);
        classDefinition.addPropertyDefinitionList(ColumnDefinitionsGenerator.createColumnDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(buildingAddress.getField(), classDefinition.getPropertyDefinitionByFieldName("streetAndNumber").getField());
    }
}
