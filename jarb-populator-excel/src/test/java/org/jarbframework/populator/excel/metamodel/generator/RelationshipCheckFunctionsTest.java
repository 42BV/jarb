package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.jarbframework.populator.excel.DefaultExcelTestDataCase;
import org.jarbframework.populator.excel.metamodel.generator.RelationshipCheckFunctions;
import org.junit.Before;
import org.junit.Test;

public class RelationshipCheckFunctionsTest extends DefaultExcelTestDataCase {
    private Metamodel metamodel;

    @Before
    public void setupRelationshipCheckFunctionsTest() throws IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        metamodel = getEntityManagerFactory().getMetamodel();

        //For code coverage purposes:
        Constructor<RelationshipCheckFunctions> constructor = RelationshipCheckFunctions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testIsSubClassOfTrue() {
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        EntityType<?> subEntity = metamodel.entity(domain.entities.SpecialCustomer.class);
        assertTrue(RelationshipCheckFunctions.isSubClassOf(subEntity, superEntity));
    }

    @Test
    public void testIsSubClassOfFalse() {
        EntityType<?> superEntity = metamodel.entity(domain.entities.Customer.class);
        EntityType<?> subEntity = metamodel.entity(domain.entities.Department.class);
        assertFalse(RelationshipCheckFunctions.isSubClassOf(subEntity, superEntity));
    }

}
