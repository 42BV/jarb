package domain;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import domain.entities.BusinessRelationshipGift;
import domain.entities.VeryImportantCustomer;

public class VeryImportantCustomerTest {

    private VeryImportantCustomer veryImportantCustomer;

    @Before
    public void setupVeryImportantCustomer() {
        veryImportantCustomer = new VeryImportantCustomer();
    }

    @Test
    public void testSetGetTitle() {
        veryImportantCustomer.setTitle("Sir.");
        assertEquals("Sir.", veryImportantCustomer.getTitle());
    }

    @Test
    public void testSetGetGifts() {
        Set<BusinessRelationshipGift> gifts = new HashSet<BusinessRelationshipGift>();
        BusinessRelationshipGift gift = new BusinessRelationshipGift();
        gift.setDescription("Wine");
        gift.setValue(12.42);
        gifts.add(gift);
        veryImportantCustomer.setGifts(gifts);
        assertEquals(gifts, veryImportantCustomer.getGifts());
    }

}
