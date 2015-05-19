package org.jarbframework.init;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.jarbframework.init.DatabasePopulator;
import org.jarbframework.init.PopulatorApplicationListener;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

public class PopulatorApplicationListenerTest {

    private PopulatorApplicationListener applicationListener;

    private DatabasePopulator initializer;
    
    private DatabasePopulator destroyer;

    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
    	initializer = mock(DatabasePopulator.class);
    	destroyer = mock(DatabasePopulator.class);
    	
        applicationListener = new PopulatorApplicationListener(initializer, destroyer);
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
        applicationListener = new PopulatorApplicationListener(initializer);
        applicationListener.onApplicationEvent(new ContextClosedEvent(applicationContext));
    }

}
