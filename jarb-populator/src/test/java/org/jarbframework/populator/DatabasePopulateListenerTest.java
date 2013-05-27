package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

public class DatabasePopulateListenerTest {

    private DatabasePopulateListener listener;

    private DatabasePopulator initializer;
    
    private DatabasePopulator destroyer;

    private ApplicationContext context;

    @Before
    public void setUp() {
    	initializer = mock(DatabasePopulator.class);
    	destroyer = mock(DatabasePopulator.class);
    	
        listener = new DatabasePopulateListener();
        listener.setInitializer(initializer);
        listener.setDestroyer(destroyer);
        
        context = mock(ApplicationContext.class);
    }

    @Test
    public void testInitialize() {
        // Upon the first context refreshed event the update is executed
        listener.onApplicationEvent(new ContextRefreshedEvent(context));
        verify(initializer, times(1)).populate();

        // Other refresh events are ignored
        listener.onApplicationEvent(new ContextRefreshedEvent(context));
        verifyNoMoreInteractions(initializer);
    }

    @Test
    public void testDestroy() {
        listener.onApplicationEvent(new ContextClosedEvent(context));
        verify(destroyer, times(1)).populate();
    }

    @Test
    public void testSkipWhenNoUpdater() {
        listener.setDestroyer(null);
        listener.onApplicationEvent(new ContextClosedEvent(context));
    }

}
