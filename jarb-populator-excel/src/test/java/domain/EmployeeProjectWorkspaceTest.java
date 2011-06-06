package domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nl.mad.hactar.common.ReflectionUtil;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.Employee;
import domain.entities.EmployeeProjectWorkspace;
import domain.entities.Project;
import domain.entities.Workspace;

public class EmployeeProjectWorkspaceTest {

    private EmployeeProjectWorkspace employeeProjectWorkspace;

    @Before
    public void setupEmployeeProjectWorkspace() {
        employeeProjectWorkspace = new EmployeeProjectWorkspace();
    }

    @Test
    public void testGetId() {
        final Long id = 1L;
        ReflectionUtil.setFieldValue(employeeProjectWorkspace, "id", id);
        assertEquals(id, employeeProjectWorkspace.getId());
    }

    @Test
    public void testSetGetEmployee() {
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setSalary(2000.00);
        employeeProjectWorkspace.setEmployee(employee);
        assertEquals(employee, employeeProjectWorkspace.getEmployee());
    }

    @Test
    public void testSetGetProject() {
        Project project = new Project();
        project.setName("Project1");
        Date date = new Date();
        date.setTime(1294909373);
        project.setStartdate(date);
        employeeProjectWorkspace.setProject(project);
        assertEquals(project, employeeProjectWorkspace.getProject());
    }

    @Test
    public void testSetGetWorkspace() {
        Workspace workspace = new Workspace();
        Address address = new Address("Schoolstraat 1", "Zoetermeer");
        workspace.setInvoiceAddress(address);
        workspace.setFloorNumber(2);
        workspace.setCubicalNumber(12);
        employeeProjectWorkspace.setWorkspace(workspace);
        assertEquals(workspace, employeeProjectWorkspace.getWorkspace());
    }

}
