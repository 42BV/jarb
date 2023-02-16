/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BeansTest {
    
    @SuppressWarnings("unused")
    private String someField;
    
    @Test
    public void testGetFields() {
        Set<String> fields = Beans.getFieldNames(BeansTest.class);
        Assertions.assertTrue(fields.contains("someField"));
    }

}
