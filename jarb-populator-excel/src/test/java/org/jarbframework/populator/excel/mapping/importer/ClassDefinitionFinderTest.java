package org.jarbframework.populator.excel.mapping.importer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.metamodel.Definition;
import org.jarbframework.populator.excel.metamodel.EmbeddableElementCollectionDefinition;
import org.jarbframework.populator.excel.metamodel.EntityDefinition;
import org.jarbframework.populator.excel.metamodel.generator.ElementCollectionDefinitionGenerator;
import org.jarbframework.populator.excel.metamodel.generator.EntityDefinitionsGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import domain.entities.Phone;

public class ClassDefinitionFinderTest {

    private Class<?> foreignClass;

    private Set<Definition> classDefinitionSet;
    private Set<Definition> emptyClassDefinitionSet;

    private EntityDefinition<?> customer;
    private EntityDefinition<?> project;
    private EntityDefinition<?> employee;
    private EmbeddableElementCollectionDefinition<?> phone;

    private ClassPathXmlApplicationContext context;
    private EntityManagerFactory entityManagerFactory;

    @Before
    public void setupClassDefinitionFinder() throws InvalidFormatException, IOException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException, ClassNotFoundException {
        emptyClassDefinitionSet = new HashSet<Definition>();

        context = new ClassPathXmlApplicationContext("test-context.xml");
        entityManagerFactory = (EntityManagerFactory) context.getBean("entityManagerFactory");

        foreignClass = domain.entities.Project.class;

        Metamodel metamodel = entityManagerFactory.getMetamodel();
        EntityType<?> persistentEntity = metamodel.entity(domain.entities.Customer.class);
        EntityType<?> foreignEntity = metamodel.entity(domain.entities.Project.class);
        EntityType<?> employeeEntity = metamodel.entity(domain.entities.Employee.class);

        classDefinitionSet = new HashSet<Definition>();
        EntityDefinitionsGenerator entityDefinitionsGenerator = new EntityDefinitionsGenerator(entityManagerFactory);
        ElementCollectionDefinitionGenerator elementCollectionDefinitionGenerator = new ElementCollectionDefinitionGenerator(entityManagerFactory);
        customer = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(persistentEntity, false);
        project = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(foreignEntity, false);
        employee = entityDefinitionsGenerator.createSingleEntityDefinitionFromMetamodel(employeeEntity, false);
        phone = (EmbeddableElementCollectionDefinition<?>) elementCollectionDefinitionGenerator.createDefinitionForSingleElementCollectionFromEntity(employee
                .property("phones"));

        classDefinitionSet.add(customer);
        classDefinitionSet.add(project);
        classDefinitionSet.add(phone);

        //For code coverage purposes:
        Constructor<DefinitionFinder> constructor = DefinitionFinder.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testFindClassDefinitionByPersistentClass() {
        EntityDefinition<?> definition = (EntityDefinition<?>) DefinitionFinder.findDefinitionByPersistentClass(classDefinitionSet,
                domain.entities.Project.class);
        assertEquals(foreignClass, definition.getDefinedClass());
        assertEquals(null, DefinitionFinder.findDefinitionByPersistentClass(classDefinitionSet, domain.entities.Employee.class));
        assertEquals(null, DefinitionFinder.findDefinitionByPersistentClass(emptyClassDefinitionSet, domain.entities.Project.class));
    }

    @Test
    public void testFindClassDefinitionByPersitentClassForEmbeddableElementCollection() {
        EmbeddableElementCollectionDefinition<?> definition = (EmbeddableElementCollectionDefinition<?>) DefinitionFinder.findDefinitionByPersistentClass(
                classDefinitionSet, domain.entities.Phone.class);
        assertEquals(Phone.class, definition.getDefinedClass());
    }
}
