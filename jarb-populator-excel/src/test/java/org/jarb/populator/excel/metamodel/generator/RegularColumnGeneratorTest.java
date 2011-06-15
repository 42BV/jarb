package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.junit.Before;
import org.junit.Test;

public class RegularColumnGeneratorTest {

    @Before
    public void setupEmbeddedColumnGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        //For code coverage purposes:
        Constructor<RegularColumnGenerator> constructor = RegularColumnGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateColumnDefinitionsForEmbeddedField() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Class<?> persistentClass = domain.entities.Project.class;

        ColumnDefinition projectName = new Column("name");
        projectName.setColumnName("name");
        projectName.setField(persistentClass.getDeclaredField("name"));

        ColumnDefinition generated = RegularColumnGenerator.createColumnDefinitionForRegularField(projectName.getField());
        assertEquals(projectName.getColumnName(), generated.getColumnName());
    }
}
