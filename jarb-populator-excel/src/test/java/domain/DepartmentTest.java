package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Department;

public class DepartmentTest {

    private Department department;

    @Before
    public void setUpDepartment() {
        department = new Department();
    }

    @Test
    public void testGetId() {
        Long id = (long) 13;
        department.setId(id);
        assertEquals(id, department.getId());
    }

    @Test
    public void testSetGetDepartmentName() {
        String departmentName = "HRM";
        department.setDepartmentName(departmentName);
        assertEquals(departmentName, department.getDepartmentName());
    }

    @Test
    public void testArgumentedConstructor() {
        Department department = new Department("Sales");
        assertEquals("Sales", department.getDepartmentName());
    }

}
