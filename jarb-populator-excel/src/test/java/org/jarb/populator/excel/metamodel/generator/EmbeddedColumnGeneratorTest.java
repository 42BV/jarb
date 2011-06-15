package org.jarb.populator.excel.metamodel.generator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jarb.populator.excel.metamodel.Column;
import org.jarb.populator.excel.metamodel.ColumnDefinition;
import org.junit.Before;
import org.junit.Test;

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
        Class<?> persistentClass = domain.entities.Employee.class;

        ColumnDefinition buildingAddress = new Column("building_address");
        buildingAddress.setColumnName("building_address");
        buildingAddress.setEmbeddedAttribute(true);
        buildingAddress.setEmbeddedObjectName("address");

        ColumnDefinition cityName = new Column("city_name");
        cityName.setColumnName("city_name");
        cityName.setEmbeddedAttribute(true);
        cityName.setEmbeddedObjectName("address");

        List<ColumnDefinition> generated = EmbeddedColumnGenerator.createColumnDefinitionsForEmbeddedField(persistentClass.getDeclaredField("address"));

        assertEquals(buildingAddress.getColumnName(), generated.get(0).getColumnName());
        assertEquals(cityName.getColumnName(), generated.get(1).getColumnName());
    }

}
