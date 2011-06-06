package domain;

import static org.junit.Assert.assertEquals;
import nl.mad.hactar.common.ReflectionUtil;

import org.junit.Before;
import org.junit.Test;

import domain.entities.Customer;
import domain.entities.ServiceLevelAgreement;

public class ServiceLevelAgreementTest {

    public ServiceLevelAgreement sla;

    @Before
    public void setupSLATest() {
        sla = new ServiceLevelAgreement();
    }

    @Test
    public void testGetID() {
        ReflectionUtil.setFieldValue(sla, "id", new Long("1"));
        assertEquals(new Long("1"), sla.getId());
    }

    @Test
    public void testSetGetTitle() {
        sla.setTitle("Business agreement B");
        assertEquals("Business agreement B", sla.getTitle());
    }

    @Test
    public void testSetGetCustomer() {
        Customer customer = new Customer();
        customer.setCompanyName("Black Mesa");
        customer.setName("Gordon Freeman");
        sla.setCustomer(customer);
        assertEquals(customer, sla.getCustomer());
    }

}
