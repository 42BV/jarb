package domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import domain.entities.NoTableAnnotation;

public class NoTableAnnotationTest {

    private NoTableAnnotation noTableAnnotation;

    @Before
    public void setUpCustomer() {
        noTableAnnotation = new NoTableAnnotation();
    }

    @Test
    public void testGetId() {
        Long id = (long) 13;
        noTableAnnotation.setId(id);
        assertEquals(id, noTableAnnotation.getId());
    }

    @Test
    public void testSetGetValue() {
        final String value = "test";
        noTableAnnotation.setColumnValue(value);
        assertEquals(value, noTableAnnotation.getColumnValue());
    }
}
