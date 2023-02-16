package nl._42.jarb.utils.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyReferenceTest {

    @Test
    public void testGetParent() {
        PropertyReference property = new PropertyReference(Object.class, "some.nested.property");
        assertArrayEquals(new String[] { "some", "nested", "property" }, property.getPath());
        assertEquals("property", property.getSimpleName());
        
        PropertyReference parent = property.getParent();
        assertArrayEquals(new String[] { "some", "nested" }, parent.getPath());
        assertEquals("nested", parent.getSimpleName());
        
        PropertyReference root = parent.getParent();
        assertArrayEquals(new String[] { "some" }, root.getPath());
        assertEquals("some", root.getSimpleName());
    }
    
    @Test
    public void testGetParentRoot() {
        Assertions.assertThrows(IllegalStateException.class, () ->
            new PropertyReference(Object.class, "property").getParent()
        );
    }
    
    @Test
    public void testHashCodeAndEquals() {
        PropertyReference a = new PropertyReference(Object.class, "property");
        PropertyReference b = new PropertyReference(Object.class, "property");
        PropertyReference c = new PropertyReference(Object.class, "other");
        PropertyReference d = new PropertyReference(String.class, "property");

        Assertions.assertTrue(a.equals(a));
        Assertions.assertTrue(a.equals(b));
        Assertions.assertFalse(a.equals(c));
        Assertions.assertFalse(a.equals(d));
        Assertions.assertFalse(a.equals("e"));
    }

    @Test
    public void testToString() {
        PropertyReference property = new PropertyReference(Object.class, "property");
        assertEquals("Object.property", property.toString());
    }

}
