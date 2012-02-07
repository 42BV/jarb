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

public class DatabaseUpdatingListenerTest {

    private DatabaseUpdatingListener listener;

    private DatabaseUpdater initializer;
    private DatabaseUpdater destroyer;

    private ApplicationContext context;

    @Before
    public void setUp() {
    	initializer = mock(DatabaseUpdater.class);
    	destroyer = mock(DatabaseUpdater.class);
    	
        listener = new DatabaseUpdatingListener(initializer, destroyer);

        context = mock(ApplicationContext.class);
    }

    @Test
    public void testInitialize() {
        // Upon the first context refreshed event the update is executed
        listener.onApplicationEvent(new ContextRefreshedEvent(context));
        verify(initializer, times(1)).update();

        // Other refresh events are ignored
        listener.onApplicationEvent(new ContextRefreshedEvent(context));
        verifyNoMoreInteractions(initializer);
    }

    @Test
    public void testDestroy() {
        listener.onApplicationEvent(new ContextClosedEvent(context));
        verify(destroyer, times(1)).update();
    }

    @Test
    public void testSkipWhenNoUpdater() {
        listener = new DatabaseUpdatingListener(initializer);
        listener.onApplicationEvent(new ContextClosedEvent(context));
    }
    
    @Test
    public void testInitializeAndRevert() {
    	RevertableDatabaseUpdater updater = mock(RevertableDatabaseUpdater.class);
    	DatabaseUpdatingListener listener = DatabaseUpdatingListener.initializeAndRevert(updater);
        
    	listener.onApplicationEvent(new ContextRefreshedEvent(context));
        verify(updater, times(1)).update();
        
        listener.onApplicationEvent(new ContextClosedEvent(context));
        verify(updater, times(1)).revert();
    }
}
