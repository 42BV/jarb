package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.DepartmentSubclass;

public class DepartmentSubClassTest {

    private DepartmentSubclass departmentSubClass;

    @Before
    public void setupDepartmentSubClass() {
        departmentSubClass = new DepartmentSubclass();
    }

    @Test
    public void testSetGetDepartmentHead() {
        departmentSubClass.setDepartmentHead("Jasper Smith");
        assertEquals("Jasper Smith", departmentSubClass.getDepartmentHead());
    }

    @Test
    public void testArgumentedConstructor() {

        DepartmentSubclass departmentSubclass = new DepartmentSubclass("Sales", "Henk Perdaan");
        assertEquals("Henk Perdaan", departmentSubclass.getDepartmentHead());
        assertEquals("Sales", departmentSubclass.getDepartmentName());
    }

}
