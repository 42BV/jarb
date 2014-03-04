package org.jarbframework.utils.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jarbframework.utils.bean.FlexibleBeanWrapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

public class ModifiableBeanTest {
    private FlexibleBeanWrapper<SomeBean> modifiableBean;

    @Before
    public void setUp() {
        modifiableBean = FlexibleBeanWrapper.instantiate(SomeBean.class);
    }

    @Test
    public void testGetValueFromField() {
        modifiableBean.getWrappedBean().hiddenProperty = "test";
        assertEquals("test", modifiableBean.getPropertyValue("hiddenProperty"));
    }

    @Test
    public void testSetValueOnField() {
        modifiableBean.setPropertyValue("hiddenProperty", "test");
        assertEquals("test", modifiableBean.getWrappedBean().hiddenProperty);
    }

    @Test
    public void testGetValueFromMethod() {
        modifiableBean.getWrappedBean().readableProperty = "test";
        assertEquals("test(from getter)", modifiableBean.getPropertyValue("readableProperty"));
    }

    @Test
    public void testSetValueByMethod() {
        modifiableBean.setPropertyValue("writableProperty", "test");
        assertEquals("test(from setter)", modifiableBean.getWrappedBean().writableProperty);
    }

    @Test(expected = NotReadablePropertyException.class)
    public void testGetNonExistingProperty() {
        modifiableBean.getPropertyValue("unknownProperty");
    }

    @Test(expected = NotWritablePropertyException.class)
    public void testSetNonExistingProperty() {
        modifiableBean.setPropertyValue("unknownProperty", "value");
    }

    @Test
    public void testHasPropertyByField() {
        assertTrue(modifiableBean.isReadableProperty("hiddenProperty"));
        assertTrue(modifiableBean.isWritableProperty("hiddenProperty"));
    }

    @Test
    public void testHasPropertyByMethod() {
        assertTrue(modifiableBean.isReadableProperty("readableProperty"));
        assertTrue(modifiableBean.isWritableProperty("writableProperty"));
    }

    @Test
    public void testDoesNotHaveProperty() {
        assertFalse(modifiableBean.isReadableProperty("unknownProperty"));
        assertFalse(modifiableBean.isWritableProperty("unknownProperty"));
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
