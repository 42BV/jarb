package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.jarb.populator.excel.metamodel.ColumnType;
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
        ColumnDefinition generated = DiscriminatorColumnGenerator.createDiscriminatorColumnDefinition(Customer.class);
        assertEquals("type", generated.getColumnName());
        assertEquals(ColumnType.DISCRIMINATOR, generated.getType());
    }

    @Test
    public void testCreateDiscriminatorColumnDefinitionWithoutAnnotation() {
        ColumnDefinition generated = DiscriminatorColumnGenerator.createDiscriminatorColumnDefinition(Workspace.class);
        assertEquals("dtype", generated.getColumnName());
        assertEquals(ColumnType.DISCRIMINATOR, generated.getType());
    }

}
