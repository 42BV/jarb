package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

public class WrappingDatabaseUpdaterTest {

    @Test
    public void testUpdate() {
        DatabaseUpdater delegate = mock(DatabaseUpdater.class);
        WrappingDatabaseUpdater updater = new WrappingDatabaseUpdaterImpl(delegate);
        
        updater.update();
        
        verify(delegate, times(1)).update();
    }
    
    @Test
    public void testRevert() {
        RevertableDatabaseUpdater delegate = mock(RevertableDatabaseUpdater.class);
        WrappingDatabaseUpdater updater = new WrappingDatabaseUpdaterImpl(delegate);
        
        updater.revert();
        
        verify(delegate, times(1)).revert();
    }
    
    @Test
    public void testRevertUnsupported() {
        DatabaseUpdater delegate = mock(DatabaseUpdater.class);
        WrappingDatabaseUpdater updater = new WrappingDatabaseUpdaterImpl(delegate);
        
        updater.revert();
        
        verifyZeroInteractions(delegate);
    }
    
    // Implementation class that should only be used for testing
    private static class WrappingDatabaseUpdaterImpl extends WrappingDatabaseUpdater {

        public WrappingDatabaseUpdaterImpl(DatabaseUpdater delegate) {
            super(delegate);
        }
        
    }
    
}
