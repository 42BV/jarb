/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils;

import org.jarbframework.utils.sample.SomeAnnotation;
import org.jarbframework.utils.sample.SomeClass;
import org.jarbframework.utils.sample.SomeInterface;
import org.junit.Assert;
import org.junit.Test;

public class ClassScannerTest {
    
    @Test
    public void testFindByType() {
        String basePackage = this.getClass().getPackage().getName();
        Assert.assertTrue(ClassScanner.getAllOfType(basePackage, SomeInterface.class).contains(SomeClass.class));
    }
    
    @Test
    public void testFindByAnnotation() {
        String basePackage = this.getClass().getPackage().getName();
        Assert.assertTrue(ClassScanner.getAllWithAnnotation(basePackage, SomeAnnotation.class).contains(SomeClass.class));
    }

}
