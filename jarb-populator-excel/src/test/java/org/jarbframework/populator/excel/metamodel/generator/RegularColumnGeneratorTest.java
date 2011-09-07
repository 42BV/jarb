package org.jarbframework.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jarbframework.populator.excel.metamodel.PropertyDefinition;
import org.jarbframework.populator.excel.metamodel.generator.RegularColumnGenerator;
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
        PropertyDefinition generated = RegularColumnGenerator.createColumnDefinitionForRegularField(persistentClass.getDeclaredField("name"));
        assertEquals("name", generated.getColumnName());
    }
}
