package domain;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jarbframework.utils.bean.DynamicBeanWrapper;
import org.junit.Before;
import org.junit.Test;

import domain.entities.BusinessRelationshipGift;
import domain.entities.Customer;
import domain.entities.ServiceLevelAgreement;
import domain.entities.VeryImportantCustomer;

public class CustomerTest {

    private Customer customer;

    @Before
    public void setUpCustomer() {
        customer = new Customer();
    }

    @Test
    public void testGetId() {
        final Long id = 1L;
        DynamicBeanWrapper.wrap(customer).setPropertyValue("id", id);
        assertEquals(id, customer.getId());
    }

    @Test
    public void testSetGetName() {
        final String name = "test";
        customer.setName(name);
        assertEquals(name, customer.getName());
    }

    @Test
    public void testSetGetCompanyName() {
        final String companyName = "test";
        customer.setCompanyName(companyName);
        assertEquals(companyName, customer.getCompanyName());
    }

    @Test
    public void testSetGetGifts() {
        VeryImportantCustomer vip = new VeryImportantCustomer();
        Set<BusinessRelationshipGift> gifts = new HashSet<BusinessRelationshipGift>();
        BusinessRelationshipGift gift = new BusinessRelationshipGift();
        gift.setDescription("Cheese fondue set");
        gift.setValue(50.42);
        gifts.add(gift);
        vip.setGifts(gifts);
        assertEquals(gifts, vip.getGifts());
    }

    @Test
    public void testSetGetSLA() {
        ServiceLevelAgreement serviceLevelAgreement = new ServiceLevelAgreement();
        serviceLevelAgreement.setTitle("SLA with Company A");
        customer.setServiceLevelAgreement(serviceLevelAgreement);
        assertEquals(serviceLevelAgreement, customer.getServiceLevelAgreement());
    }

}
