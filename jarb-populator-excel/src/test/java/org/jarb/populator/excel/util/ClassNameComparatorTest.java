package org.jarb.populator.excel.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ClassNameComparatorTest {

    private List<Class<?>> classList;

    @Before
    public void setupTestCompare() {
        classList = new ArrayList<Class<?>>();
    }

    @Test
    public void testCompare() {
        classList.add(domain.entities.Project.class);
        classList.add(domain.entities.Customer.class);
        classList.add(domain.entities.Employee.class);
        Collections.sort(classList, new ClassNameComparator());

        assertEquals("domain.entities.Customer", classList.get(0).getName());
        assertEquals("domain.entities.Employee", classList.get(1).getName());
        assertEquals("domain.entities.Project", classList.get(2).getName());
    }

}
