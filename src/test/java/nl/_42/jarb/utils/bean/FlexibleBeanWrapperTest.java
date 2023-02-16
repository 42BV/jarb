package nl._42.jarb.utils.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlexibleBeanWrapperTest {

    private FlexibleBeanWrapper beanWrapper;

    @BeforeEach
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

    @Test
    public void testGetNonExistingProperty() {
        Assertions.assertThrows(NotReadablePropertyException.class, () ->
            beanWrapper.getPropertyValue("unknownProperty")
        );
    }

    @Test
    public void testSetNonExistingProperty() {
        Assertions.assertThrows(NotWritablePropertyException.class, () ->
            beanWrapper.setPropertyValue("unknownProperty", "value")
        );
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
