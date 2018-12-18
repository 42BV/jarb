package nl._42.jarb.utils.bean;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

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
    
    @Test(expected = IllegalStateException.class)
    public void testGetParentRoot() {
        new PropertyReference(Object.class, "property").getParent();
    }
    
    @Test
    public void testHashCodeAndEquals() {
        PropertyReference a = new PropertyReference(Object.class, "property");
        PropertyReference b = new PropertyReference(Object.class, "property");
        PropertyReference c = new PropertyReference(Object.class, "other");
        PropertyReference d = new PropertyReference(String.class, "property");
        
        Assert.assertTrue(a.equals(a));
        Assert.assertTrue(a.equals(b));
        Assert.assertFalse(a.equals(c));
        Assert.assertFalse(a.equals(d));
        Assert.assertFalse(a.equals("e"));
        Assert.assertFalse(a.equals(null));
    }

    @Test
    public void testToString() {
        PropertyReference property = new PropertyReference(Object.class, "property");
        Assert.assertEquals("Object.property", property.toString());
    }

}
