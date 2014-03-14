/*
 * (C) 2013 42 bv (www.42.nl). All rights reserved.
 */
package org.jarbframework.utils.spring;

import javax.sql.DataSource;

import org.jarbframework.utils.DataSourceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test case for {@link BeanLocator}.
 *
 * @author Jeroen van Schagen
 * @since Mar 14, 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataSourceConfig.class)
public class BeanLocatorTest {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private BeanLocator beanLocator;
    
    @Before
    public void setUp() {
        beanLocator = new BeanLocator(applicationContext);
    }

    @Test
    public void testFindBeanById() {
        Assert.assertNotNull(beanLocator.findBean(DataSource.class, "dataSource"));
    }
    
    @Test
    public void testFindBeanByType() {
        Assert.assertNotNull(beanLocator.findBean(DataSource.class, null));
    }
    
    @Test
    public void testFindUniqueBeanByType() {
        Assert.assertNotNull(beanLocator.getSingleBean(DataSource.class));
    }
    
    @Test
    public void testFindUniqueBeanByFallback() {
        ApplicationContext mockedApplicationContext = Mockito.mock(ApplicationContext.class);
        BeanLocator beanLocator = new BeanLocator(mockedApplicationContext);

        DataSource dataSource = Mockito.mock(DataSource.class);
        
        Mockito.when(mockedApplicationContext.getBean(DataSource.class)).thenThrow(new NoUniqueBeanDefinitionException(DataSource.class));
        Mockito.when(mockedApplicationContext.getBean("dataSource", DataSource.class)).thenReturn(dataSource);

        Assert.assertEquals(dataSource, beanLocator.getSingleBean(DataSource.class));
    }

}
