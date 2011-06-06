package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.UnannotatedClass;

public class UnannotatedClassTest {

    private UnannotatedClass unannotatedClass;

    @Before
    public void setUpCustomer() {
        unannotatedClass = new UnannotatedClass();
    }

    @Test
    public void testGetId() {
        Long id = (long) 13;
        unannotatedClass.setId(id);
        assertEquals(id, unannotatedClass.getId());
    }

    @Test
    public void testSetGetValue() {
        final String value = "test";
        unannotatedClass.setColumnValue(value);
        assertEquals(value, unannotatedClass.getColumnValue());
    }
}
