package org.jarbframework.populator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public class ClassNameDatabaseUpdaterTest {

	@Test
	public void testInstantiateDelegate() {
		MyCustomUpdater delegate = new MyCustomUpdater();
		
		ApplicationContext context = Mockito.mock(ApplicationContext.class);
		AutowireCapableBeanFactory factory = Mockito.mock(AutowireCapableBeanFactory.class);
		Mockito.when(context.getAutowireCapableBeanFactory()).thenReturn(factory);
		Mockito.when(factory.createBean(MyCustomUpdater.class)).thenReturn(delegate);
		
		ClassNameDatabaseUpdater updater = new ClassNameDatabaseUpdater(MyCustomUpdater.class.getName());
		PropertyAccessorFactory.forDirectFieldAccess(updater).setPropertyValue("applicationContext", context);
		
		assertTrue(delegate == updater.getDelegate());
		// When invoked twice, the value is cached
		assertTrue(delegate == updater.getDelegate());
	}
	
	@Test(expected = RuntimeException.class)
	public void testUnknownClass() {
		new ClassNameDatabaseUpdater("some.unknown.WierdClass").update();
	}

	public static class MyCustomUpdater implements DatabaseUpdater {

		@Override
		public void update() {
			// Do nothing
		}
		
	}
	
}
