package org.jarbframework.populator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class WrappingDatabaseUpdaterTest {

	private DatabaseUpdater updaterDelegate = mock(DatabaseUpdater.class);
	private DatabaseUpdater reverterDelegate = mock(DatabaseUpdater.class);
	
	private WrappingDatabaseUpdater wrappingUpdater = new WrappingDatabaseUpdater(updaterDelegate, reverterDelegate);
	
	@Test
	public void testUpdate() {
		wrappingUpdater.update();

        verify(updaterDelegate, times(1)).update();
	}
	
	@Test
	public void testRevert() {
		wrappingUpdater.revert();

        verify(reverterDelegate, times(1)).update();
	}

}
