package org.jarbframework.populator;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.beans.PropertyAccessorFactory.forDirectFieldAccess;

import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class ClassNameDatabaseUpdaterTest {

    @Test
    public void testInstantiateDelegate() {
        MyCustomUpdater delegate = new MyCustomUpdater();

        ApplicationContext context = mock(ApplicationContext.class);
        AutowireCapableBeanFactory factory = mock(AutowireCapableBeanFactory.class);
        when(context.getAutowireCapableBeanFactory()).thenReturn(factory);
        when(factory.createBean(MyCustomUpdater.class)).thenReturn(delegate);

        ClassNameDatabaseUpdater updater = new ClassNameDatabaseUpdater(MyCustomUpdater.class.getName());
        forDirectFieldAccess(updater).setPropertyValue("applicationContext", context);

        assertTrue(delegate == updater.getDelegate());
        assertTrue(delegate == updater.getDelegate()); // When invoked twice, the value is cached
    }
    
    @Test
    public void testNoClass() {
    	assertNull(new ClassNameDatabaseUpdater("").getDelegate());
    }

    @Test(expected = RuntimeException.class)
    public void testUnknownClass() {
        new ClassNameDatabaseUpdater("some.unknown.WierdClass").getDelegate();
    }

    public static class MyCustomUpdater implements DatabaseUpdater {

        @Override
        public void update() {
            // Do nothing
        }

    }

}
