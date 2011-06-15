package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.junit.Before;
import org.junit.Test;

public class DiscriminatorColumnGeneratorTest {

    private Class<?> persistentClass;

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
        persistentClass = domain.entities.Customer.class;
        ColumnDefinition discriminatorColumn = new Column("type");
        discriminatorColumn.setDiscriminatorColumn(true);
        discriminatorColumn.setColumnName("type");

        ColumnDefinition generated = DiscriminatorColumnGenerator.createDiscriminatorColumnDefinition(persistentClass);
        assertEquals(discriminatorColumn.getColumnName(), generated.getColumnName());
    }

    @Test
    public void testCreateDiscriminatorColumnDefinitionWithoutAnnotation() {
        persistentClass = domain.entities.Workspace.class;

        ColumnDefinition discriminatorColumn = new Column("dtype");
        discriminatorColumn.setDiscriminatorColumn(true);
        discriminatorColumn.setColumnName("dtype");

        ColumnDefinition generated = DiscriminatorColumnGenerator.createDiscriminatorColumnDefinition(persistentClass);
        assertEquals(discriminatorColumn.getColumnName(), generated.getColumnName());
    }

}
