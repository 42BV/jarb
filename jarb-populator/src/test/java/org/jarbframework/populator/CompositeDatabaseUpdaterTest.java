package org.jarbframework.populator;

import static org.jarbframework.populator.CompositeDatabaseUpdater.composite;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

public class CompositeDatabaseUpdaterTest {

    @Test
    public void testUpdate() {
        DatabaseUpdater head = mock(DatabaseUpdater.class);
        DatabaseUpdater tail = mock(DatabaseUpdater.class);
        CompositeDatabaseUpdater composite = composite(head, tail);
        
        composite.update();
        
        verify(head, times(1)).update();
        verify(tail, times(1)).update();
    }
    
    @Test
    public void testRevert() {
        RevertableDatabaseUpdater head = mock(RevertableDatabaseUpdater.class);
        DatabaseUpdater body = mock(DatabaseUpdater.class);
        RevertableDatabaseUpdater tail = mock(RevertableDatabaseUpdater.class);
        CompositeDatabaseUpdater composite = composite(head, body, tail);
        
        composite.revert();
        
        verify(tail, times(1)).revert();
        verifyZeroInteractions(body);
        verify(head, times(1)).revert();
    }
    
}
