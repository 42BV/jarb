package domain;

import static org.junit.Assert.assertEquals;

import org.jarb.utils.bean.ModifiableBean;
import org.junit.Before;
import org.junit.Test;

import domain.entities.BusinessRelationshipGift;

public class BusinessRelationshipGiftTest {

    private BusinessRelationshipGift gift;

    @Before
    public void setupBusinessRelationshipGift() {
        gift = new BusinessRelationshipGift();
    }

    @Test
    public void testGetId() {
        final Long id = 1L;
        ModifiableBean.wrap(gift).setPropertyValue("id", id);
        assertEquals(id, gift.getId());
    }

    @Test
    public void testSetGetDescription() {
        gift.setDescription("Wine");
        assertEquals("Wine", gift.getDescription());
    }

    @Test
    public void testSetGetValue() {
        Double value = 99.95;
        gift.setValue(value);
        assertEquals(value, gift.getValue());
    }

}
