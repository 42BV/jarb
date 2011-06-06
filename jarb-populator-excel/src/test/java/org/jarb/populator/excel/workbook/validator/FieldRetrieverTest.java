package org.jarb.populator.excel.workbook.validator;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;

public class FieldRetrieverTest {

    @Before
    public void constructor() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        //For code coverage purposes:
        Constructor<FieldRetriever> constructor = FieldRetriever.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void tryToGetFieldFromClass() {
        Field field = FieldRetriever.tryToGetFieldFromClass("departmentName", domain.entities.Department.class);
        assertEquals("departmentName", field.getName());
    }

}
