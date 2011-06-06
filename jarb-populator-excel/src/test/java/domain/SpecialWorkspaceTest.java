package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.DepartmentSubclass;
import domain.entities.SpecialWorkspace;

public class SpecialWorkspaceTest {
    private SpecialWorkspace specialWorkspace;

    @Before
    public void setUpCustomer() {
        specialWorkspace = new SpecialWorkspace();
    }

    @Test
    public void testSetGetCompanyLocation() {
        final String location = "test";
        specialWorkspace.setWorkspaceOwner(location);
        assertEquals(location, specialWorkspace.getWorkspaceOwner());
    }

    @Test
    public void testSetGetDepartmentSubclass() {
        DepartmentSubclass departmentSubclass = new DepartmentSubclass();
        departmentSubclass.setDepartmentHead("Ron Oudshoorn");
        departmentSubclass.setDepartmentName("Sales");
        specialWorkspace.setDepartmentSubclass(departmentSubclass);
        assertEquals(departmentSubclass, specialWorkspace.getDepartmentSubclass());
    }
}
