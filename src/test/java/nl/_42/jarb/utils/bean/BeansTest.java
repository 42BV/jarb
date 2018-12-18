/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.bean;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class BeansTest {
    
    @SuppressWarnings("unused")
    private String someField;
    
    @Test
    public void testGetFields() {
        Set<String> fields = Beans.getFieldNames(BeansTest.class);
        Assert.assertTrue(fields.contains("someField"));
    }

}
