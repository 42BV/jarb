/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.sample;

import org.jarbframework.sample.ApplicationConfig;
import org.jarbframework.sample.DispatcherServletInitializer;
import org.jarbframework.sample.WebMvcConfig;
import org.junit.Assert;
import org.junit.Test;

public class DispatcherServletInitializerTest {
    
    private DispatcherServletInitializer initializer = new DispatcherServletInitializer();

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
