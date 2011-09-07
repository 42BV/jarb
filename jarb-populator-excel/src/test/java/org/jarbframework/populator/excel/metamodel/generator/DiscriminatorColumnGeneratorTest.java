package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jarbframework.populator.excel.metamodel.generator.DiscriminatorColumnGenerator;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;
import domain.entities.Workspace;

public class DiscriminatorColumnGeneratorTest {

    @Before
    public void setupDiscriminatorColumnGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        //For code coverage purposes:
        Constructor<DiscriminatorColumnGenerator> constructor = DiscriminatorColumnGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateDiscriminatorColumnDefinitionWithAnnotation() {
        assertEquals("type", DiscriminatorColumnGenerator.getDiscriminatorColumnName(Customer.class));
    }

    @Test
    public void testCreateDiscriminatorColumnDefinitionWithoutAnnotation() {
        assertEquals("dtype", DiscriminatorColumnGenerator.getDiscriminatorColumnName(Workspace.class));
    }

}
