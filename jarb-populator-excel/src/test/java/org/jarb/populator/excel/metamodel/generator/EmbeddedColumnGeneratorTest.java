package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Employee;

public class EmbeddedColumnGeneratorTest {

    @Before
    public void setupEmbeddedColumnGeneratorTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        //For code coverage purposes:
        Constructor<EmbeddedColumnGenerator> constructor = EmbeddedColumnGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateColumnDefinitionsForEmbeddedField() throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        List<ColumnDefinition> generated = EmbeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(Employee.class.getDeclaredField("address"));
        assertEquals("building_address", generated.get(0).getColumnName());
        assertEquals("city_name", generated.get(1).getColumnName());
    }

}
