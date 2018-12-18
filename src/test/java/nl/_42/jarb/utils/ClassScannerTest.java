/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils;

import nl._42.jarb.utils.sample.SomeAnnotation;
import nl._42.jarb.utils.sample.SomeClass;
import nl._42.jarb.utils.sample.SomeInterface;

import nl._42.jarb.utils.sample.SomeAnnotation;
import nl._42.jarb.utils.sample.SomeClass;
import nl._42.jarb.utils.sample.SomeInterface;
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
