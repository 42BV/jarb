package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.jarb.utils.bean.ModifiableBean;
import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;
import domain.entities.Project;
import domain.entities.Project.Type;

public final class ProjectTest {

    private Project project;

    @Before
    public void setUpProject() {
        project = new Project();
    }

    @Test
    public void testGetId() {
        final Long id = 1L;
        new ModifiableBean(project).setPropertyValue("id", id);
        assertEquals(id, project.getId());
    }

    @Test
    public void testSetGetName() {
        final String name = "test";
        project.setName(name);
        assertEquals(name, project.getName());
    }

    @Test
    public void testSetGetStartDate() {
        final Date startdate = Date.valueOf("2010-01-01");
        project.setStartdate(startdate);
        assertEquals(startdate, project.getStartdate());
    }

    @Test
    public void testSetGetCustomer() {
        Customer customer = new Customer();
        project.setCustomer(customer);
        assertEquals(customer, project.getCustomer());
    }

    @Test
    public void testSetGetActiveProject() {
        project.setActiveProject(true);
        assertTrue(project.getActiveProject());
    }

    @Test
    public void testSetGetType() {
        project.setType(Type.DEVELOPMENT);
        assertEquals("DEVELOPMENT", project.getType().toString());
    }

    @SuppressWarnings("static-access")
    @Test
    public void testTypeTest() {
        project.getType().DEVELOPMENT.test();
        project.getType().INTEGRATION.test();
    }
}
