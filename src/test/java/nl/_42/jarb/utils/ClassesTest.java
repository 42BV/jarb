package nl._42.jarb.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassesTest {
    
    private static final String UNKNOWN_CLASS_NAME = "some.unknown.ClassName";
    private static final String UNKNOWN_PACKAGE_NAME = "some.unknown.package";

    @Test
    public void testForName() {
        Assertions.assertEquals(Classes.class, Classes.forName(Classes.class.getName()));
    }
    
    @Test
    public void testForNameNotOnClasspath() {
        Assertions.assertThrows(RuntimeException.class, () ->
            Classes.forName(UNKNOWN_CLASS_NAME)
        );
    }
    
    @Test
    public void testOnClasspath() {
        Assertions.assertTrue(Classes.hasClass(Classes.class.getName()));
        Assertions.assertFalse(Classes.hasClass(UNKNOWN_CLASS_NAME));
    }
    
    @Test
    public void testHasPackage() {
        Assertions.assertTrue(Classes.hasPackage(Classes.class.getPackage().getName()));
        Assertions.assertFalse(Classes.hasPackage(UNKNOWN_PACKAGE_NAME));
    }

}
