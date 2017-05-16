package org.jarbframework.utils;

import org.junit.Assert;
import org.junit.Test;

public class ClassesTest {
    
    private static final String UNKNOWN_CLASS_NAME = "some.unknown.ClassName";
    private static final String UNKNOWN_PACKAGE_NAME = "some.unknown.package";

    @Test
    public void testForName() {
        Assert.assertEquals(Classes.class, Classes.forName(Classes.class.getName()));
    }
    
    @Test(expected = RuntimeException.class)
    public void testForNameNotOnClasspath() {
        Classes.forName(UNKNOWN_CLASS_NAME);
    }
    
    @Test
    public void testOnClasspath() {
        Assert.assertTrue(Classes.hasClass(Classes.class.getName()));
        Assert.assertFalse(Classes.hasClass(UNKNOWN_CLASS_NAME));
    }
    
    @Test
    public void testHasPackage() {
        Assert.assertTrue(Classes.hasPackage(Classes.class.getPackage().getName()));
        Assert.assertFalse(Classes.hasPackage(UNKNOWN_PACKAGE_NAME));
    }

}
