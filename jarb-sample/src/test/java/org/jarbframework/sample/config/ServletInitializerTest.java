/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample.config;

import org.junit.Assert;
import org.junit.Test;

public class ServletInitializerTest {
    
    private ServletInitializer initializer = new ServletInitializer();

    @Test
    public void testGetRootConfigClasses() {
        Assert.assertArrayEquals(new Class<?>[] { ApplicationConfig.class }, initializer.getRootConfigClasses());
    }
    
    @Test
    public void testGetServletConfigClasses() {
        Assert.assertArrayEquals(new Class<?>[] { WebMvcConfig.class }, initializer.getServletConfigClasses());
    }
    
    @Test
    public void testGetServletMappings() {
        Assert.assertArrayEquals(new String[] { "/" }, initializer.getServletMappings());
    }

}
