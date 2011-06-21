package org.jarb.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jarb.utils.BeanPropertyUtils;
import org.junit.Before;
import org.junit.Test;

public class BeanPropertyHandlerTest {
    private SomeBean bean;
    
    @Before
    public void setUp() {
        bean = new SomeBean();
    }
    
    @Test
    public void testGetValueFromField() {
        bean.hiddenProperty = "test";
        assertEquals("test", BeanPropertyUtils.getValue(bean, "hiddenProperty"));
    }
    
    @Test
    public void testGetValueFromMethod() {
        bean.readableProperty = "test";
        assertEquals("test(from getter)", BeanPropertyUtils.getValue(bean, "readableProperty"));
    }
    
    @Test
    public void testGetNonExistingProperty() {
        try {
            BeanPropertyUtils.getValue(bean, "unknownProperty");
            fail("Expected an exception because the property does not exist");
        } catch(IllegalArgumentException e) {
            assertEquals("Property 'unknownProperty' does not exist in SomeBean.", e.getMessage());
        }
    }
    
    @Test
    public void testSetValueOnField() {
        BeanPropertyUtils.setValue(bean, "hiddenProperty", "test");
        assertEquals("test", bean.hiddenProperty);
    }
    
    @Test
    public void testSetValueByMethod() {
        BeanPropertyUtils.setValue(bean, "writableProperty", "test");
        assertEquals("test(from setter)", bean.writableProperty);
    }
    
    @Test
    public void testSetNonExistingProperty() {
        try {
            BeanPropertyUtils.setValue(bean, "unknownProperty", "value");
            fail("Expected an exception because the property does not exist");
        } catch(IllegalArgumentException e) {
            assertEquals("Property 'unknownProperty' does not exist in SomeBean.", e.getMessage());
        }
    }
    
    @Test
    public void testHasPropertyByField() {
        assertTrue(BeanPropertyUtils.hasProperty(bean, "hiddenProperty"));
    }

    @Test
    public void testHasPropertyByMethod() {
        assertTrue(BeanPropertyUtils.hasProperty(bean, "readableProperty"));
        assertTrue(BeanPropertyUtils.hasProperty(bean, "writableProperty"));
    }
    
    @Test
    public void testDoesNotHaveProperty() {
        assertFalse(BeanPropertyUtils.hasProperty(bean, "unknownProperty"));
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
