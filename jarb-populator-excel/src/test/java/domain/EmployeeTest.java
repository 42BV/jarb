package domain;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.jarbframework.utils.bean.DynamicBeanWrapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Address;
import domain.entities.CompanyCar;
import domain.entities.Employee;
import domain.entities.Project;
import domain.entities.CompanyVehicle.Gearbox;

public final class EmployeeTest {

    private Employee employee;

    @Before
    public void setUpEmployee() {
        employee = new Employee();
    }

    @Test
    public void testGetId() {
        final Integer id = 1;
        DynamicBeanWrapper.wrap(employee).setPropertyValue("id", id);
        assertEquals(id, employee.getId());
    }

    @Test
    public void testSetGetName() {
        final String name = "test";
        employee.setName(name);
        assertEquals(name, employee.getName());
    }

    @Test
    public void testSetGetSalary() {
        final Double salary = 10.00;
        employee.setSalary(salary);
        assertEquals(salary, employee.getSalary());
    }

    @Test
    public void testSetGetProjects() {
        Set<Project> projects = new HashSet<Project>();
        projects.add(DynamicBeanWrapper.instantiate(Project.class).setPropertyValue("id", 1L).getWrappedBean());
        projects.add(DynamicBeanWrapper.instantiate(Project.class).setPropertyValue("id", 2L).getWrappedBean());
        employee.setProjects(projects);
        assertEquals(projects, employee.getProjects());
    }

    @Test
    public void testSetGetHomeAddress() {
        Address homeAddress = new Address("Schoolstraat 1", "Zoetermeer");
        employee.setAddress(homeAddress);
        assertEquals(homeAddress, employee.getAddress());
    }

    @Test
    public void testSetGetDateOfBirth() {
        Date dateOfBirth = new Date(636685200);
        employee.setDateOfBirth(dateOfBirth);
        assertEquals(dateOfBirth, employee.getDateOfBirth());
    }

    @Test
    public void testSetGetSalaryScale() {
        Character type = new Character('a');
        employee.setSalaryScale(type);
        assertEquals(type, employee.getSalaryScale());
    }

    @Test
    public void testSetGetVehicles() {
        CompanyCar alto = new CompanyCar();
        alto.setAirbags(true);
        alto.setGearBox(Gearbox.MANUAL);
        alto.setMilesPerGallon(40);
        alto.setPrice(10000.00);
        alto.setModel("Suzuki Alto");

        employee.setVehicle(alto);
        assertEquals(alto, employee.getVehicle());
    }

}
