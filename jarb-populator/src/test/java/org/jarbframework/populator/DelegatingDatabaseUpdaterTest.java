package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;

public class DelegatingDatabaseUpdaterTest {
	
	private DatabaseUpdater delegate = mock(DatabaseUpdater.class);
	private DelegatingDatabaseUpdater updater = new DelegatingDatabaseUpdater() {
		
		@Override
		protected DatabaseUpdater getDelegate() {
			return delegate;
		}
		
	};

    @Test
    public void testUpdate() {
        updater.update();
        
        verify(delegate, times(1)).update();
    }
    
    @Test
    public void testCannotUpdateWhenNoDelegate() {
    	new DelegatingDatabaseUpdater() {
    		
    		@Override
    		protected DatabaseUpdater getDelegate() {
    			return null;
    		}
    		
    	}.update();
    }

    @Test
    public void testRevert() {
        final RevertableDatabaseUpdater delegate = mock(RevertableDatabaseUpdater.class);
        
        new DelegatingDatabaseUpdater() {
    		
    		@Override
    		protected DatabaseUpdater getDelegate() {
    			return delegate;
    		}
    		
    	}.revert();
    	
        verify(delegate, times(1)).revert();
    }

    @Test
    public void testRevertUnsupported() {
        updater.revert();
        
        verifyZeroInteractions(delegate);
    }

}
