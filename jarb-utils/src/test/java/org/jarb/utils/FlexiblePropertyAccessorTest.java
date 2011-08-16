package org.jarb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

public class FlexiblePropertyAccessorTest {
    private SomeBean bean;
    private FlexiblePropertyAccessor accessor;

    @Before
    public void setUp() {
        bean = new SomeBean();
        accessor = new FlexiblePropertyAccessor(bean);
    }

    @Test
    public void testGetValueFromField() {
        bean.hiddenProperty = "test";
        assertEquals("test", accessor.getPropertyValue("hiddenProperty"));
    }

    @Test
    public void testGetValueFromMethod() {
        bean.readableProperty = "test";
        assertEquals("test(from getter)", accessor.getPropertyValue("readableProperty"));
    }

    @Test(expected = NotReadablePropertyException.class)
    public void testGetNonExistingProperty() {
        accessor.getPropertyValue("unknownProperty");
    }

    @Test
    public void testSetValueOnField() {
        accessor.setPropertyValue("hiddenProperty", "test");
        assertEquals("test", bean.hiddenProperty);
    }

    @Test
    public void testSetValueByMethod() {
        accessor.setPropertyValue("writableProperty", "test");
        assertEquals("test(from setter)", bean.writableProperty);
    }

    @Test(expected = NotWritablePropertyException.class)
    public void testSetNonExistingProperty() {
        accessor.setPropertyValue("unknownProperty", "value");
    }

    @Test
    public void testHasPropertyByField() {
        assertTrue(accessor.isReadableProperty("hiddenProperty"));
        assertTrue(accessor.isWritableProperty("hiddenProperty"));
    }

    @Test
    public void testHasPropertyByMethod() {
        assertTrue(accessor.isReadableProperty("readableProperty"));
        assertTrue(accessor.isWritableProperty("writableProperty"));
    }

    @Test
    public void testDoesNotHaveProperty() {
        assertFalse(accessor.isReadableProperty("unknownProperty"));
        assertFalse(accessor.isWritableProperty("unknownProperty"));
    }

    public static class SomeBean {
        private String hiddenProperty;
        private String readableProperty;
        private String writableProperty;

        public String getReadableProperty() {
            return readableProperty + "(from getter)";
        }

        public void setWritableProperty(String writableProperty) {
            this.writableProperty = writableProperty + "(from setter)";
        }
    }

}
