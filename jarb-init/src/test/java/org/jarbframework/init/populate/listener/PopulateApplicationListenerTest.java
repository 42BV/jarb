package org.jarbframework.init.populate.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.jarbframework.init.populate.DatabasePopulator;
import org.jarbframework.init.populate.listener.PopulateApplicationListener;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

public class PopulateApplicationListenerTest {

    private PopulateApplicationListener applicationListener;

    private DatabasePopulator initializer;
    
    private DatabasePopulator destroyer;

    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
    	initializer = mock(DatabasePopulator.class);
    	destroyer = mock(DatabasePopulator.class);
    	
        applicationListener = new PopulateApplicationListener();
        applicationListener.setInitializer(initializer);
        applicationListener.setDestroyer(destroyer);
        
        applicationContext = mock(ApplicationContext.class);
    }

    @Test
    public void testInitialize() {
        // Upon the first context refreshed event the update is executed
        applicationListener.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
        verify(initializer, times(1)).execute();

        // Other refresh events are ignored
        applicationListener.onApplicationEvent(new ContextRefreshedEvent(applicationContext));
        verifyNoMoreInteractions(initializer);
    }

    @Test
    public void testDestroy() {
        applicationListener.onApplicationEvent(new ContextClosedEvent(applicationContext));
        verify(destroyer, times(1)).execute();
    }

    @Test
    public void testSkipWhenNoUpdater() {
        applicationListener.setDestroyer(null);
        applicationListener.onApplicationEvent(new ContextClosedEvent(applicationContext));
    }

}
