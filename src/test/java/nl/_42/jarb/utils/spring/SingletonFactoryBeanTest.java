/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.utils.spring;

import nl._42.jarb.utils.SingletonFactoryBean;

import nl._42.jarb.utils.SingletonFactoryBean;
import nl._42.jarb.utils.sample.SomeClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SingletonFactoryBeanTest {
    
    private MySigletonFactoryBean factoryBean;
    
    @Before
    public void setUp() {
        factoryBean = new MySigletonFactoryBean();
    }

    @Test
    public void testIsSingleton() {
        Assert.assertTrue(factoryBean.isSingleton());
    }
    
    @Test
    public void testGetType() {
        Assert.assertEquals(SomeClass.class, factoryBean.getObjectType());
    }
    
    @Test
    public void testGetInstance() throws Exception {
        SomeClass someBean = factoryBean.getObject();
        Assert.assertNotNull(someBean);
        Assert.assertEquals(someBean, factoryBean.getObject());
    }

    private static class MySigletonFactoryBean extends SingletonFactoryBean<SomeClass> {
        
        @Override
        protected SomeClass createObject() throws Exception {
            return new SomeClass();
        }
        
    }

}
