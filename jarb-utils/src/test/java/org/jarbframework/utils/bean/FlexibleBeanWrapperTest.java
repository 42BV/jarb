package org.jarbframework.utils.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

public class FlexibleBeanWrapperTest {

    private FlexibleBeanWrapper beanWrapper;

    @Before
    public void setUp() {
        beanWrapper = new FlexibleBeanWrapper(new SomeBean());
    }

    @Test
    public void testGetValueFromField() {
        ((SomeBean) beanWrapper.getWrappedBean()).hiddenProperty = "test";
        assertEquals("test", beanWrapper.getPropertyValue("hiddenProperty"));
    }

    @Test
    public void testSetValueOnField() {
        beanWrapper.setPropertyValue("hiddenProperty", "test");
        assertEquals("test", ((SomeBean) beanWrapper.getWrappedBean()).hiddenProperty);
    }

    @Test
    public void testGetValueFromMethod() {
        ((SomeBean) beanWrapper.getWrappedBean()).readableProperty = "test";
        assertEquals("test(from getter)", beanWrapper.getPropertyValue("readableProperty"));
    }

    @Test
    public void testSetValueByMethod() {
        beanWrapper.setPropertyValue("writableProperty", "test");
        assertEquals("test(from setter)", ((SomeBean) beanWrapper.getWrappedBean()).writableProperty);
    }

    @Test(expected = NotReadablePropertyException.class)
    public void testGetNonExistingProperty() {
        beanWrapper.getPropertyValue("unknownProperty");
    }

    @Test(expected = NotWritablePropertyException.class)
    public void testSetNonExistingProperty() {
        beanWrapper.setPropertyValue("unknownProperty", "value");
    }

    @Test
    public void testHasPropertyByField() {
        assertTrue(beanWrapper.isReadableProperty("hiddenProperty"));
        assertTrue(beanWrapper.isWritableProperty("hiddenProperty"));
    }

    @Test
    public void testHasPropertyByMethod() {
        assertTrue(beanWrapper.isReadableProperty("readableProperty"));
        assertTrue(beanWrapper.isWritableProperty("writableProperty"));
    }

    @Test
    public void testDoesNotHaveProperty() {
        assertFalse(beanWrapper.isReadableProperty("unknownProperty"));
        assertFalse(beanWrapper.isWritableProperty("unknownProperty"));
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
