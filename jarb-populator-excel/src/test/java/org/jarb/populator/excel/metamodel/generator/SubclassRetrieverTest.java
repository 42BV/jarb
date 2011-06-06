package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarb.populator.excel.DefaultExcelTestDataCase;
import org.junit.Before;
import org.junit.Test;

import domain.entities.UnannotatedClass;

public class SubclassRetrieverTest extends DefaultExcelTestDataCase {

    private Metamodel metamodel;

    @Before
    public void setupClassDefinitionsGeneratorTest() throws InvalidFormatException, IOException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        metamodel = getEntityManagerFactory().getMetamodel();

        //For code coverage purposes:
        Constructor<SubclassRetriever> constructor = SubclassRetriever.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetSubClassEntities() {
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        Set<EntityType<?>> subEntities = new HashSet<EntityType<?>>();
        EntityType<?> subEntity = metamodel.entity(domain.entities.SpecialCustomer.class);
        EntityType<?> subSubEntity = metamodel.entity(domain.entities.VeryImportantCustomer.class);
        subEntities.add(subEntity);
        subEntities.add(subSubEntity);
        assertEquals(subEntities, SubclassRetriever.getSubClassEntities(superEntity, metamodel.getEntities()));
    }

    @Test
    public void testReturnSubClassMapping() throws ClassNotFoundException {
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        Class<?> specialCustomer = domain.entities.SpecialCustomer.class;

        Set<EntityType<?>> subClassEntities = SubclassRetriever.getSubClassEntities(superEntity, metamodel.getEntities());
        Map<String, Class<?>> subClassMap = SubclassRetriever.getSubClassMapping(subClassEntities);
        assertEquals(specialCustomer, subClassMap.get("special"));
    }

    @Test
    public void testGetSubClassEntitiesNull() {
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        Set<EntityType<?>> subEntity = new HashSet<EntityType<?>>();
        assertEquals(subEntity, SubclassRetriever.getSubClassEntities(superEntity, null));
    }

    @Test
    public void testGetDiscriminatorValue() {
        assertEquals("special", SubclassRetriever.getDiscriminatorValue(domain.entities.SpecialCustomer.class));
    }

    @Test
    public void testGetDiscriminatorValueFromEntityAnnotation() {
        assertEquals("customers", SubclassRetriever.getDiscriminatorValue(domain.entities.Customer.class));
    }

    @Test
    public void testGetDiscriminatorValueFromEntityAnnotationNotAvailable() {
        assertEquals("UnannotatedClass", SubclassRetriever.getDiscriminatorValue(domain.entities.UnannotatedClass.class));
    }

    @Test
    public void testTryToReadEntityNameAnnotationForDiscriminatorValueNull() throws SecurityException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<SubclassRetriever> subclassRetriever = SubclassRetriever.class;
        Class<?> persistentClass = UnannotatedClass.class;

        Class<?>[] paramTypes = { java.lang.Class.class };
        Object[] parameters = { persistentClass };
        Method tryToReadEntityNameAnnotationForDiscriminatorValue = subclassRetriever.getDeclaredMethod("tryToReadEntityNameAnnotationForDiscriminatorValue",
                paramTypes);
        tryToReadEntityNameAnnotationForDiscriminatorValue.setAccessible(true);
        assertEquals(null, tryToReadEntityNameAnnotationForDiscriminatorValue.invoke(null, parameters));
    }

}
