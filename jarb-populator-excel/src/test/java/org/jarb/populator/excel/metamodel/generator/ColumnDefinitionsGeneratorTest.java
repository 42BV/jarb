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
import org.jarb.populator.excel.metamodel.PropertyDefinition;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        Class<?> persistentClass = domain.entities.Department.class;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        PropertyDefinition departmentName = new Column("departmentName");
        departmentName.setColumnName("department_name");
        departmentName.setField(persistentClass.getDeclaredField("departmentName"));
        ClassDefinition classDefinition = new ClassDefinition(persistentClass);
        classDefinition.addColumnDefinitionList(ColumnDefinitionsGenerator.createColumnDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(departmentName.getField(), classDefinition.getColumnDefinitionByFieldName("departmentName").getField());
    }

    @Test
    public void testCreateColumnDefinitionsWithEmbeddables() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException,
            NoSuchFieldException {
        Class<?> persistentClass = domain.entities.Employee.class;
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> entity = metamodel.entity(persistentClass);
        Set<EntityType<?>> subClassEntities = new HashSet<EntityType<?>>();
        PropertyDefinition buildingAddress = new Column("streetAndNumber");
        buildingAddress.setColumnName("streetAndNumber");
        buildingAddress.setField(domain.entities.Address.class.getDeclaredField("streetAndNumber"));
        ClassDefinition classDefinition = new ClassDefinition(persistentClass);
        classDefinition.addColumnDefinitionList(ColumnDefinitionsGenerator.createColumnDefinitions(subClassEntities, entity, persistentClass));
        assertEquals(buildingAddress.getField(), classDefinition.getColumnDefinitionByFieldName("streetAndNumber").getField());
    }
}
