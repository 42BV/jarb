package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.generator.SuperclassRetriever;
import org.junit.Before;
import org.junit.Test;

public class SuperclassRetrieverTest extends DefaultExcelTestDataCase {
    private Metamodel metamodel;

    @Before
    public void setupClassDefinitionsGeneratorTest() throws InvalidFormatException, IOException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        metamodel = getEntityManagerFactory().getMetamodel();

        //For code coverage purposes:
        Constructor<SuperclassRetriever> constructor = SuperclassRetriever.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetSuperClass() {
        EntityType<?> subEntity = metamodel.entity(domain.entities.SpecialCustomer.class);
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        assertEquals(superEntity, SuperclassRetriever.getSuperClassEntity(subEntity, metamodel.getEntities()));
    }

    @Test
    public void testGetSuperClassButFailTo() {
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);
        assertEquals(null, SuperclassRetriever.getSuperClassEntity(entity, metamodel.getEntities()));
    }

    @Test
    public void testgetSuperClassEntitiesNull() {
        EntityType<?> entity = metamodel.entity(domain.entities.Project.class);
        assertEquals(null, SuperclassRetriever.getSuperClassEntity(entity, null));
    }
}
